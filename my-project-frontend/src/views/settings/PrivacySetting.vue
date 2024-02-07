<script setup>

import Card from "@/components/Card.vue";
import {Hide, Key, Lock, Switch} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";

const form = reactive({
  password: '',
  new_password: '',
  new_password_repeat: ''
})

const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.new_password) {
    callback(new Error("两次输入的新密码不一致"))
  } else {
    callback()
  }
}
const rules = {
  password: [
    {required: true, message: '请输入原密码', trigger: 'blur'},
  ],
  new_password: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  new_password_repeat: [
    {required: true, validator: validateNewPassword, trigger: ['blur', 'change']}
  ]
}
const formRef = ref()
const valid = ref(false)
const onValidate = (prop, isValid) => valid.value = isValid

function resetPassword() {
  formRef.value.validate(valid => {
    if (valid) {
      post('/api/user/change-password', form, () => {
        ElMessage.success('密码修改成功')
        // 重置表单
        formRef.value.resetFields()
      })
    }
  })
}

const saving = ref(true)
const privacy = reactive({
  phone: false,
  wx: false,
  qq: false,
  email: false,
  gender: false
})
get('/api/user/privacy', data => {
  Object.assign(privacy, data)
  saving.value = false
})

function savePrivacy(type, status) {
  saving.value = true
  post('/api/user/save-privacy', {
    type: type,
    status: status
  }, () => {
    ElMessage.success('隐私设置修改成功')
    saving.value = false
  })
}
</script>

<template>
  <div style="margin: auto;max-width: 600px">
    <div style="margin-top: 20px;">
      <card
          v-loading="saving"
          :icon="Hide"
          title="隐私设置"
          desc="在这里设置哪些内容可以被其他人看到，请注重隐私"
      >
        <div class="checkbox-list">
          <el-checkbox @change="savePrivacy('phone', privacy.phone)"
                       v-model="privacy.phone">公开展示我的手机号
          </el-checkbox>
          <el-checkbox @change="savePrivacy('email', privacy.email)"
                       v-model="privacy.email">公开展示我的电子邮件地址
          </el-checkbox>
          <el-checkbox @change="savePrivacy('wx', privacy.wx)"
                       v-model="privacy.wx">公开展示我的微信号
          </el-checkbox>
          <el-checkbox @change="savePrivacy('qq', privacy.qq)"
                       v-model="privacy.qq">公开展示我的QQ号
          </el-checkbox>
          <el-checkbox @change="savePrivacy('gender', privacy.gender)"
                       v-model="privacy.gender">公开展示我的性别
          </el-checkbox>
        </div>
      </card>
      <card :icon="Key" style="margin: 20px 0" title="修改密码" desc="可以在这里修改密码，请修改后务必牢记">
        <el-form
            ref="formRef"
            :rules="rules"
            :model="form"
            label-width="100"
            style="margin: 20px"
            @validate="onValidate"
        >
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.password" placeholder="当前密码"
                      maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password" placeholder="新密码"
                      maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="new_password_repeat">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password_repeat"
                      placeholder="重新输入新密码" maxlength="16"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button @click="resetPassword" type="success" :icon="Switch">立即重置密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list {
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>
