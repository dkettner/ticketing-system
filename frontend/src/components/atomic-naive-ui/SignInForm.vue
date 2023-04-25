<script setup>
  import { useRouter } from 'vue-router';
  import { NCard, NTabs, NTabPane, NForm, NFormItemRow, NInput, NButton, NIcon, useNotification } from 'naive-ui';
  import { GlassesOutline, Glasses } from "@vicons/ionicons5";
  import { ref } from 'vue';
  import axios from 'axios';
  import { useSessionStore } from '../../stores/session';

  const router = useRouter();
  const notificationAgent = useNotification();
  const sessionStore = useSessionStore();
  const signInFormRef = ref(null);
  const signUpFormRef = ref(null);

  const signUpFormValue = ref({
    userPostData: {
      name: '',
      email: '',
      password: ''
    },
    reenteredPassword: ''
  })
  const signInFormValue = ref({
    credentials: {
      email: '',
      password: ''
    }
  });

  const signInRules = {
    credentials : {
      email: {
        required: true,
        trigger: ["blur", "input"],
        message: "Please input email"
      },
      password: {
        required: true,
        trigger: ["blur", "input"],
        message: "Please input password"
      }
    }
  }
  const signUpRules = {
    userPostData: {
      name: {
        required: true,
        trigger: ["blur", "input"],
        message: "Please input name"
      },
      email: {
        required: true,
        trigger: ["blur", "input"],
        message: "Please input email"
      },
      password: {
        required: true,
        trigger: ["blur", "input"],
        message: "Please input password"
      }
    },
    reenteredPassword: [
      {
        required: true,
        trigger: ["blur", "input"],
        message: "Please input password"
      },
      {
        validator: validatePasswordSame,
        message: "Password is not same as re-entered password!",
        trigger: ["blur", "password-input"]
      }
    ]
  }
  function validatePasswordSame(rule, value) {
      return value === signUpFormValue.value.userPostData.password;
    }

  function sendNotification(_title, _content) {
    notificationAgent.create({
      title: _title,
      content: _content
    });
  }
  async function handleSignInClick(clickEvent) {
    signInFormRef.value?.validate(
      async (errors) => {
        if (!errors) {
          try {
            const loginResult = await sessionStore.login(signInFormValue.value.credentials.email, signInFormValue.value.credentials.password);
            if (loginResult.isLoginSuccessful) {
              router.push('/projects');
            } else {
              sendNotification(
                "Error",
                loginResult.message
              )
            }
          } catch(error) {
            console.log(error)
            sendNotification("Error", error.response.data);
          }
        } else {}
      } 
    )
  }
  async function handleSignUpClick(clickEvent) {
    signUpFormRef.value?.validate(
      async (errors) => {
        if (!errors) {
          try {
            const postUserResponse = await axios.post('/users', signUpFormValue.value.userPostData);
      
            sendNotification(
              "Success", 
              "Created your new account with E-Mail:\n" + 
              postUserResponse.data.email + "\n\n" + 
              "You will now be redirected to Sign In ..."
            );
            setTimeout(() => {
              router.go();
            }, 5000);
          } catch(error) {
            console.log(error)
            sendNotification("Error", error.response.data);
          }
        } else { console.log(errors) }
      }
    )
    
  }
</script>

<template>
  <n-card style="border-radius: 10px; box-shadow: 5px 5px 5px lightgrey; background-color: #fdfdfd;">
    <div style="margin-left: -20px;">
      <img width="275" src="../../assets/logo_ticketing.png" />
    </div>
    
    <n-tabs
      class="card-tabs"
      default-value="signin"
      size="large"
      animated
      style="margin: 0 -4px;"
      pane-style="padding-left: 4px; padding-right: 4px; box-sizing: border-box;"
    >
      <n-tab-pane name="signin" tab="Sign in">
        <n-form
          ref="signInFormRef"
          :model="signInFormValue"
          :rules="signInRules"
        >
          <n-form-item-row label="E-Mail" path="credentials.email">
            <n-input v-model:value="signInFormValue.credentials.email"/>
          </n-form-item-row>
          <n-form-item-row label="Password" path="credentials.password">
            <n-input
              type="password"
              v-model:value="signInFormValue.credentials.password"
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
        <n-form
          ref="signUpFormRef"
          :model="signUpFormValue"
          :rules="signUpRules"
        >
          <n-form-item-row label="Name" path="userPostData.name">
            <n-input v-model:value="signUpFormValue.userPostData.name"/>
          </n-form-item-row>
          <n-form-item-row label="E-Mail" path="userPostData.email">
            <n-input v-model:value="signUpFormValue.userPostData.email"/>
          </n-form-item-row>
          <n-form-item-row label="Password" path="userPostData.password">
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
          <n-form-item-row label="Reenter Password" path="reenteredPassword">
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
