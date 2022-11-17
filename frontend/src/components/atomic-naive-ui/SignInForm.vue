<script setup>
  import { useRouter } from 'vue-router';
  import { NCard, NTabs, NTabPane, NForm, NFormItemRow, NInput, NButton, NIcon, useNotification } from 'naive-ui';
  import { GlassesOutline, Glasses } from "@vicons/ionicons5";
  import { ref } from 'vue';
  import axios from 'axios';
  import { sleep } from 'seemly';

  const router = useRouter()
  const notificationAgent = useNotification();

  const signUpFormValue = ref({
    userPostData: {
      name: '',
      email: '',
      password: ''
    },
    reenteredPassword: ''
  })
  
  const signInFormValue = ref({
    authenticationPostData: {
      email: '',
      password: ''
    }
  });

  function sendNotification(_title, _content) {
    notificationAgent.create({
      title: _title,
      content: _content
    });
  }
  function handleSignInClick(clickEvent) {
    router.push('/dashboard')
  }
  async function handleSignUpClick(clickEvent) {
    try {
      const postUserResponse = await axios.post('http://localhost:8080/users', signUpFormValue.value.userPostData);
      
      sendNotification(
        "Success", 
        "Created your new account with E-Mail:\n" + 
        postUserResponse.data.email + "\n\n" + 

        "You will now be redirected to Sign In ..."
      );
      
      setTimeout(() => {
        router.go();
      }, 5000);
      
    } catch(exception) {
      sendNotification("Error", exception);
    }
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
      ref="tabPosition"
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
