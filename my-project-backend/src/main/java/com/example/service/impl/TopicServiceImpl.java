package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.CommentVO;
import com.example.entity.vo.response.TopicDetailVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTopVO;
import com.example.mapper.*;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    TopicTypeMapper mapper;

    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

    @Resource
    AccountMapper accountMapper;

    @Resource
    AccountDetailsMapper accountDetailsMapper;

    @Resource
    AccountPrivacyMapper accountPrivacyMapper;

    @Resource
    TopicCommentMapper commentMapper;

    @Resource
    StringRedisTemplate template;

    @Resource
    NotificationService notificationService;

    // 预先获取帖子类型的ID的Set集合 用于检查所要发的帖子类型是否存在
    private Set<Integer> types = null;

    // 在Spring容器初始化时获取帖子类型的ID的Set集合
    @PostConstruct
    private void initTypes() {
        types = this.listTypes()
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
    }

    // 获取所有的帖子类型列表
    @Override
    public List<TopicType> listTypes() {
        return mapper.selectList(null);
    }

    /**
     * @param uid 用户ID
     * @param vo  帖子创建的VO对象
     * @return String 如果发帖成功则返回null 否则返回错误信息
     * @description 创建帖子
     */
    @Override
    public String createTopic(int uid, TopicCreateVO vo) {
        if (textLimitCheck(vo.getContent(), 15, 20000))
            return "帖子内容过长或过短，限制为[15, 20000]字";
        if (!types.contains(vo.getType()))
            return "帖子类型非法！";
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if (!flowUtils.limitPeriodCounterCheck(key, 3, 3600))
            return "发帖过于频繁，请稍后再试！";
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if (this.save(topic)) {
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误导致发帖失败，请联系管理员！";
        }
    }

    /**
     * @param uid 用户ID
     * @param vo  帖子更新的VO对象
     * @return String 如果更新成功则返回null 否则返回错误信息
     * @description 更新帖子
     */
    @Override
    public String updateTopic(int uid, TopicUpdateVO vo) {
        if (textLimitCheck(vo.getContent(), 15, 20000))
            return "帖子内容过长或过短，限制为[15, 20000]字";
        if (!types.contains(vo.getType()))
            return "帖子类型非法！";
        baseMapper.update(null, Wrappers.<Topic>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("type", vo.getType())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toJSONString())
        );
        return null;
    }

    /**
     * @description 创建评论
     * @param uid 用户ID
     * @param vo 添加评论的VO对象
     * @return String 如果创建成功则返回null 否则返回错误信息
     */
    @Override
    public String createComment(int uid, AddCommentVO vo) {
        String key = Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        if (textLimitCheck(JSONObject.parseObject(vo.getContent()), 5, 2000))
            return "评论内容过长或过短，限制为[5, 2000]字！";
        // 限制一分钟内最多发两条评论
        if (!flowUtils.limitPeriodCounterCheck(key, 2, 60))
            return "评论过于频繁，请稍后再试！";
        TopicComment comment = new TopicComment();
        comment.setUid(uid);
        BeanUtils.copyProperties(vo, comment);
        comment.setTime(new Date());
        commentMapper.insert(comment);
        Topic topic = baseMapper.selectById(vo.getTid());
        Account account = accountMapper.selectById(uid);
        if (vo.getQuote() > 0) {
            TopicComment com = commentMapper.selectById(vo.getQuote());
            // 如果引用的评论不是自己的评论 则通知被引用的评论的作者 （这是评论别人的评论）
            if (!Objects.equals(com.getUid(), account.getId())) {
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的的评论被回复",
                        account.getUsername() + "回复了您的评论，快去看看吧！",
                        "success",
                        "/index/topic-detail/" + com.getTid()
                );
            }
            // 如果评论的帖子不是自己的帖子 则通知帖子的作者 （这是评论别人的帖子）
        } else if (!Objects.equals(topic.getUid(), account.getId())) {
            notificationService.addNotification(
                    topic.getUid(),
                    "您的帖子有了新的的评论",
                    account.getUsername() + "回复了你发的帖子：" + topic.getTitle() + "，快去看看吧！",
                    "success",
                    "/index/topic-detail/" + topic.getId()
            );
        }
        return null;
    }

    /**
     * @description 分页获取帖子的评论
     * @param tid 帖子ID
     * @param pageNumber 页码
     * @return List<CommentVO> 评论列表
     */
    @Override
    public List<CommentVO> comments(int tid, int pageNumber) {
        Page<TopicComment> page = Page.of(pageNumber, 10);
        commentMapper.selectPage(page, Wrappers.<TopicComment>query()
                .eq("tid", tid)
        );
        return page.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(dto, vo);
            // 如果有引用评论 则获取引用的评论内容
            if (dto.getQuote() > 0) {
                TopicComment comment = commentMapper.selectOne(Wrappers.<TopicComment>query()
                        .eq("id", dto.getQuote())
                        .orderByDesc("time")
                );
                if (comment != null) {
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {
                    });
                    vo.setQuote(builder.toString());
                } else {
                    vo.setQuote("原评论已被删除");
                }
            }
            vo.setUser(fillUserDetailsByPrivacy(new CommentVO.User(), dto.getUid()));
            return vo;
        }).toList();
    }

    // 根据评论ID和用户ID删除评论
    @Override
    public void deleteComment(int id, int uid) {
        commentMapper.delete(Wrappers.<TopicComment>query()
                .eq("id", id)
                .eq("uid", uid)
        );
    }

    /**
     * @description 分页获取帖子列表
     * @param pageNumber 页码
     * @param type 帖子类型
     * @return List<TopicPreviewVO> 帖子列表
     */
    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
        if (list != null) return list;
        Page<Topic> page = Page.of(pageNumber, 10);
        if (type == 0)
            baseMapper.selectPage(page, Wrappers.<Topic>query().orderByDesc("time"));
        else
            baseMapper.selectPage(page, Wrappers.<Topic>query().eq("type", type).orderByDesc("time"));
        List<Topic> topics = page.getRecords();
        if (topics.isEmpty()) return null;
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }

    /**
     * @description 获取置顶帖子列表
     * @return List<TopicTopVO> 置顶帖子列表
     */
    @Override
    public List<TopicTopVO> listTopTopics() {
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> {
            TopicTopVO vo = new TopicTopVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }


    /**
     * @description 获取用户收藏的帖子列表
     * @param uid 用户ID
     * @return List<TopicPreviewVO> 帖子预览列表
     */
    @Override
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.userCollectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVO vo = new TopicPreviewVO();
                    BeanUtils.copyProperties(topic, vo);
                    return vo;
                })
                .toList();
    }

    /**
     * @description 获取帖子的详细信息 包括查看访问该帖子的用户是否点赞、收藏
     * @param tid 帖子ID
     * @param uid 用户ID
     * @return TopicDetailVO 帖子详细信息VO对象
     */
    @Override
    public TopicDetailVO getTopic(int tid, int uid) {
        TopicDetailVO vo = new TopicDetailVO();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVO.Interact interact = new TopicDetailVO.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailVO.User user = new TopicDetailVO.User();
        vo.setUser(fillUserDetailsByPrivacy(user, topic.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<TopicComment>query()
                .eq("tid", tid)
        ));
        return vo;
    }

    /*
      由于论坛交互数据(如点赞、收藏等) 更新可能会非常频繁
      更新信息实时到MySQL不太现实，所以需要用Redis做缓冲并在合适的时机一次性入库一段时间内的全部数据
      当数据更新到来时，会创建一个新的定时任务，此任务会在一段时间之后执行
      将全部Redis暂时缓存的信息一次性加入到数据库，从而缓解MySQL压力，
      如果在定时任务已经设定期间又有新的更新到来，仅更新Redis不创建新的延时任务
     */
    /**
     * @description 交互操作
     * @param interact 交互
     * @param state 交互的目标状态（例如 已点赞->未点赞/未点赞->已点赞）
     */
    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        // 加锁 防止写入Redis的同时有新数据进入
        synchronized (type.intern()) {
            // 将交互信息写入Redis（存入hash 键不可重复 重复则覆盖）
            template.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);
        }

    }

    // 定时任务 用于将Redis中的交互信息每隔一段时间一次性写入MySQL
    private final Map<String, Boolean> state = new HashMap<>();
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    private void saveInteractSchedule(String type) {
        if (!state.getOrDefault(type, false)) {
            state.put(type, true);
            service.schedule(() -> {
                this.saveInteract(type);
                state.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }

    // 将交互信息从Redis中取出并一次性写入MySQL入库
    private void saveInteract(String type) {
        synchronized (type.intern()) {
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            template.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString()))
                    check.add(Interact.parseInteract(k.toString(), type));
                else
                    uncheck.add(Interact.parseInteract(k.toString(), type));
            });
            if (!check.isEmpty())
                baseMapper.addInteract(check, type);
            if (!uncheck.isEmpty())
                baseMapper.deleteInteract(uncheck, type);
            // 入库后清空Redis中的相关缓存
            template.delete(type);
        }
    }

    // 从Redis中获取交互信息
    // 如果Redis中没有则从MySQL中获取
    private boolean hasInteract(int tid, int uid, String type) {
        String key = tid + ":" + uid;
        if (template.opsForHash().hasKey(type, key))
            return Boolean.parseBoolean(template.opsForHash().entries(type).get(key).toString());
        return baseMapper.userInteractCount(tid, uid, type) > 0;
    }

    /**
     * @param target 目标对象
     * @param uid    用户ID
     * @return T 填充后的目标对象
     * @description 根据用户的隐私设置填充用户的详细信息
     */
    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        AccountDetails details = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy privacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = privacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(details, target, ignores);
        return target;
    }

    /**
     * @param topic 待转换的Topic对象
     * @return TopicPreviewVO 转换后的TopicPreviewVO对象
     * @description 将Topic对象转换为TopicPreviewVO对象
     */
    private TopicPreviewVO resolveToPreview(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo);
        BeanUtils.copyProperties(topic, vo);
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        this.shortContent(ops, previewText, obj -> images.add(obj.toString()));
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    /**
     * @param ops          内容中的opsJSON数组
     * @param previewText  最终的预览文本
     * @param imageHandler content中图片的处理方法
     * @description 从帖子的内容中提取出预览文本和图片（最多展示300字）
     */
    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler) {
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(imageHandler);
            }
        }
    }

    /**
     * @param object 帖子的文本内容
     * @param max    帖子文本最大长度
     * @param min    帖子文本最小长度
     * @return boolean 是否符合长度要求 true为不符合 false为符合
     * @description 检查帖子的文本内容（delta类型文本）是否符合长度要求
     */
    private boolean textLimitCheck(JSONObject object, int min, int max) {
        if (object == null) return true;
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > max) return true;
        }
        return length < min;
    }
}
