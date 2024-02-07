<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill";
import {ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const props = defineProps({
    show: Boolean,
    tid: String,
    quote: Object,
})

const content = ref()

const emit = defineEmits(['close', 'comment'])

const init = () => content.value = new Delta()

function submitComment() {
    if (deltaToText(content.value).length < 5) {
        ElMessage.warning('评论最少五个字！')
        return
    }
    if (deltaToText(content.value).length > 2000) {
        ElMessage.warning('评论字数超出最大限制2000字！')
        return
    }
    post('/api/forum/add-comment', {
        tid: props.tid,
        quote: props.quote ? props.quote.id : -1,
        content: JSON.stringify(content.value),
    }, () => {
        ElMessage.success('评论发表成功！')
        emit('comment')
    })
}

// delta转换为简单的预览文本
function deltaToSimpleText(delta) {
    let str = deltaToText(JSON.parse(delta))
    if (str.length > 35) str = str.substring(0, 35) + '...'
    return str
}

// delta转换为纯文本
function deltaToText(delta) {
    if (!delta?.ops) return ""
    let str = ""
    for (let op of delta.ops)
        str += op.insert
    return str.replace(/\s/g, "")
}
</script>

<template>
    <div>
        <el-drawer :model-value="show"
                   @close="emit('close')"
                   :title="quote ? `发表对评论：${deltaToSimpleText(quote.content)} 的回复` : '发表评论'"
                   @open="init"
                   direction="btt"
                   :size="270"
                   :close-on-click-modal="false">
            <div>
                <div>
                    <quill-editor style="height: 120px;"
                                  v-model:content="content"
                                  placeholder="良言一句三冬暖，恶语伤人六月寒...请友善发表评论"
                    />
                </div>
                <div style="margin-top: 10px;display: flex">
                    <div style="flex: 1;color: grey;font-size: 13px">
                        当前字数 {{ deltaToText(content).length }} (最大2000字)
                    </div>
                    <el-button type="success" plain @click="submitComment">发表评论</el-button>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer) {
    width: 800px;
    margin: 20px auto;
    border-radius: 10px 10px 10px 10px;
}

:deep(.el-drawer__header) {
    margin: 0;
}

:deep(.el-drawer__body) {
    padding: 10px;
}
</style>
