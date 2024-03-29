<script setup>

import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useStore()

const registerTime = computed(() =>
    new Date(store.user.registerTime).toLocaleString()
)

const desc = ref('')
const baseFormRef = ref()
const emailFormRef = ref()
const baseForm = reactive({
    username: '',
    gender: 1,
    phone: '',
    qq: '',
    wx: '',
    desc: '',
})
const emailForm = reactive({
    email: '',
    code: ''
})
const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
        callback(new Error('用户名不能包含特殊字符，只能包含英文/数字'))
    } else {
        callback()
    }
}
const validatePhone = (rule, value, callback) => {
    if (!/^(1[3-9]\d{9})?$/.test(value) && value) {
        callback(new Error('请输入正确的中国大陆11位手机号'))
    } else {
        callback()
    }
}
const validateWX = (rule, value, callback) => {
    if (!/^[a-zA-Z0-9]+$/.test(value) && value) {
        callback(new Error('微信号不能包含特殊字符，只能包含英文/数字'))
    } else {
        callback()
    }
}
const validateQQ = (rule, value, callback) => {
    if (!/^[1-9][0-9]{4,}$/.test(value) && value) {
        callback(new Error('请输入正确的QQ号'))
    } else {
        callback()
    }
}
const rules = {
    username: [
        {validator: validateUsername, trigger: ['blur', 'change']},
        {min: 4, max: 16, message: '用户名的长度必须在4-16个字符之间', trigger: ['blur', 'change']},
    ],
    phone: [
        {validator: validatePhone, trigger: ["change"]},
    ],
    email: [
        {required: true, message: '请输入邮件地址', trigger: 'blur'},
        {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
    ],
    wx: [
        {validator: validateWX, trigger: ['blur', 'change']},
    ],
    qq: [
        {validator: validateQQ, trigger: ['blur', 'change']},
    ]
}

const loading = reactive({
    form: true,
    base: false,
})

function saveDetails() {
    baseFormRef.value.validate(isValid => {
        if (isValid) {
            loading.base = true
            post('/api/user/save-details', baseForm, () => {
                ElMessage.success('用户信息保存成功')
                store.user.username = baseForm.username
                desc.value = baseForm.desc
                loading.base = false
            }, (message) => {
                ElMessage.warning(message)
                loading.base = false
            })
        }
    })
}

get('/api/user/details', data => {
    baseForm.username = store.user.username
    baseForm.gender = data.gender
    baseForm.phone = data.phone
    baseForm.qq = data.qq
    baseForm.wx = data.wx
    baseForm.desc = desc.value = data.desc
    emailForm.email = store.user.email
    loading.form = false
})

const coldTime = ref(0)
const isEmailValid = ref(true)
const onValidate = (prop, isValid) => {
    if (prop === 'email')
        isEmailValid.value = isValid
}

function sendModifyEmailCode() {
    emailFormRef.value.validate(isValid => {
        if (isValid) {
            coldTime.value = 60
            get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`, () => {
                ElMessage.success(`验证码已成功发送到邮箱：${emailForm.email}，请注意查收`)
                const handle = setInterval(() => {
                    coldTime.value--
                    if (coldTime.value === 0) {
                        clearInterval(handle)
                    }
                }, 1000)
            }, (message) => {
                ElMessage.warning(message)
                coldTime.value = 0
            })
        }
    })
}

function modifyEmail() {
    emailFormRef.value.validate(isValid => {
        if (isValid) {
            post('/api/user/modify-email', emailForm, () => {
                ElMessage.success('邮箱修改成功')
                store.user.email = emailForm.email
                emailForm.code = ''
            })
        }
    })
}

function beforeAvatarUpload(rawFile) {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.warning('仅支持上传jpg/png格式的图片')
        return false
    } else if (rawFile.size > 1024 * 1024 * 2) {
        ElMessage.warning('图片大小不能超过2MB')
        return false
    }
    return true
}

function uploadSuccess(response) {
    ElMessage.success('头像上传成功')
    store.user.avatar = response.data
}
</script>

<template>
    <div style="display: flex; max-width: 1000px; margin: auto">
        <div class="setting-left">
            <card
                :icon="User"
                title="账号信息设置"
                desc="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息"
                v-loading="loading.form"
            >
                <el-form
                    ref="baseFormRef"
                    :model="baseForm"
                    :rules="rules"
                    label-position="top"
                    style="margin: 0 10px 10px 10px"
                >
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="baseForm.username" maxlength="16"/>
                    </el-form-item>
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="baseForm.gender">
                            <el-radio :label="0">男</el-radio>
                            <el-radio :label="1">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="手机号" prop="phone">
                        <el-input v-model="baseForm.phone" maxlength="11"/>
                    </el-form-item>
                    <el-form-item label="QQ号" prop="qq">
                        <el-input v-model="baseForm.qq"  maxlength="13"/>
                    </el-form-item>
                    <el-form-item label="微信号" prop="wx">
                        <el-input v-model="baseForm.wx" maxlength="20"/>
                    </el-form-item>
                    <el-form-item label="个人简介" prop="desc">
                        <el-input type="textarea" maxlength="200" :rows="6" v-model="baseForm.desc"/>
                    </el-form-item>
                    <div>
                        <el-button
                            :icon="Select"
                            @click="saveDetails"
                            type="success"
                            :loading="loading.base"
                        >
                            保存用户信息
                        </el-button>
                    </div>
                </el-form>
            </card>
            <card style="margin-top: 10px" :icon="Message" title="电子邮件设置"
                  desc="您可以在这里修改绑定的电子邮件地址">
                <el-form
                    ref="emailFormRef"
                    :model="emailForm"
                    :rules="rules"
                    @validate="onValidate"
                    label-position="top"
                    style="margin: 0 10px 10px 10px"
                >
                    <el-form-item label="电子邮件" prop="email">
                        <el-input v-model="emailForm.email"/>
                    </el-form-item>
                    <el-form-item prop="code">
                        <el-row style="width: 100%;" :gutter="10">
                            <el-col :span="18">
                                <el-input v-model="emailForm.code" placeholder="请获取验证码"/>
                            </el-col>
                            <el-col :span="6">
                                <el-button
                                    type="success"
                                    plain
                                    style="width: 100%;"
                                    @click="sendModifyEmailCode"
                                    :disabled="!isEmailValid || coldTime > 0"
                                >
                                    {{ coldTime > 0 ? `请稍候${coldTime}秒` : '获取验证码' }}
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>
                    <div>
                        <el-button :icon="Refresh" type="success" @click="modifyEmail">确认更改邮箱</el-button>
                    </div>
                </el-form>
            </card>
        </div>
        <div class="setting-right">
            <div style="position: sticky; top: 20px">
                <card>
                    <div style="text-align: center;padding: 5px 15px 0 15px">
                        <div>
                            <el-avatar :size="70"
                                       :src="store.avatarUrl"/>
                            <div style="margin: 5px 0">
                                <el-upload
                                    :action="axios.defaults.baseURL + '/api/image/avatar'"
                                    :show-file-list="false"
                                    :before-upload="beforeAvatarUpload"
                                    :on-success="uploadSuccess"
                                    :headers="accessHeader()"
                                >
                                    <el-button size="small" round>修改头像</el-button>
                                </el-upload>
                            </div>
                            <div style="font-weight: bold">你好，{{ store.user.username }}</div>
                        </div>
                        <el-divider style="margin: 10px 0"/>
                        <div style="font-size: 14px;color:grey;padding: 10px">
                            {{ desc || '这个人很懒，没有留下个人简介' }}
                        </div>
                    </div>
                </card>
                <card style="margin-top: 10px;font-size: 14px">
                    <div>账号注册时间：{{ registerTime }}</div>
                    <div style="color: grey">欢迎加入！希望你能在这里有所收获</div>
                </card>
            </div>
        </div>
    </div>
</template>

<style scoped>
.setting-left {
    flex: 1;
    margin: 20px;
}

.setting-right {
    width: 300px;
    margin: 20px 30px 20px 0;
}
</style>
