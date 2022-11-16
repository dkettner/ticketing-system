<script setup>
  import { useRouter } from 'vue-router';
  import { NCard, NTabs, NTabPane, NForm, NFormItemRow, NInput, NButton, NIcon } from 'naive-ui';
  import { GlassesOutline, Glasses } from "@vicons/ionicons5";
import { ref } from 'vue';
import axios from 'axios';

  const router = useRouter()

  const signUpFormValue = ref({
    userPostData: {
      name: '',
      email: '',
      password: ''
    },
    reenteredPassword: ''
  })

  function handleSignInClick(clickEvent) {
    router.push('/dashboard')
  }
  async function handleSignUpClick(clickEvent) {
    console.log(signUpFormValue.value);
    const postUserResult = await axios.post('http://localhost:8080/users', signUpFormValue.value.userPostData);
    console.log(postUserResult);
    router.push('/dashboard')
  }
</script>

<template>
  <n-card>
    <n-tabs
      class="card-tabs"
      default-value="signin"
      size="large"
      animated
      style="margin: 0 -4px"
      pane-style="padding-left: 4px; padding-right: 4px; box-sizing: border-box;"
    >
      <n-tab-pane name="signin" tab="Sign in">
        <n-form>
          <n-form-item-row label="E-Mail">
            <n-input />
          </n-form-item-row>
          <n-form-item-row label="Password">
            <n-input
              type="password"
              show-password-on="click"
              placeholder="Please Input"
              :maxlength="32"
            >
              <template #password-visible-icon>
                <n-icon :size="16" :component="GlassesOutline" />
              </template>
              <template #password-invisible-icon>
                <n-icon :size="16" :component="Glasses" />
              </template>
            </n-input>
          </n-form-item-row>
        </n-form>
        <n-button @click="handleSignInClick" type="primary" block primary strong>
          Sign In
        </n-button>
      </n-tab-pane>
      <n-tab-pane name="signup" tab="Sign up">
        <n-form>
          <n-form-item-row label="Username">
            <n-input v-model:value="signUpFormValue.userPostData.name"/>
          </n-form-item-row>
          <n-form-item-row label="E-Mail">
            <n-input v-model:value="signUpFormValue.userPostData.email"/>
          </n-form-item-row>
          <n-form-item-row label="Password">
            <n-input
              type="password"
              v-model:value="signUpFormValue.userPostData.password"
              show-password-on="click"
              placeholder="Please Input"
              :maxlength="32"
            >
              <template #password-visible-icon>
                <n-icon :size="16" :component="GlassesOutline" />
              </template>
              <template #password-invisible-icon>
                <n-icon :size="16" :component="Glasses" />
              </template>
            </n-input>
          </n-form-item-row>
          <n-form-item-row label="Reenter Password">
            <n-input
              type="password"
              v-model:value="signUpFormValue.reenteredPassword"
              show-password-on="click"
              placeholder="Please Input"
              :maxlength="32"
            >
              <template #password-visible-icon>
                <n-icon :size="16" :component="GlassesOutline" />
              </template>
              <template #password-invisible-icon>
                <n-icon :size="16" :component="Glasses" />
              </template>
            </n-input>
          </n-form-item-row>
        </n-form>
        <n-button @click="handleSignUpClick" type="primary" block primary strong>
          Sign Up
        </n-button>
      </n-tab-pane>
    </n-tabs>
  </n-card>
</template>
  
<style scoped>
  .card-tabs .n-tabs-nav--bar-type {
    padding-left: 4px;
  }
</style>
