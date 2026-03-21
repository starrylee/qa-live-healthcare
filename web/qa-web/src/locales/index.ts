import { createI18n } from 'vue-i18n';
import zhCN from './zh-cn.json';
import enUS from './en-us.json';

const messages = {
  'zh-cn': zhCN,
  'en-us': enUS
};

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('locale') || 'zh-cn',
  fallbackLocale: 'zh-cn',
  messages
});

export default i18n;

export const availableLocales = [
  { code: 'zh-cn', name: '中文' },
  { code: 'en-us', name: 'English' }
];
