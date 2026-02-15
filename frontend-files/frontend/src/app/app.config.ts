import {ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter} from '@angular/router';
import {RECAPTCHA_V3_SITE_KEY, RecaptchaV3Module} from 'ng-recaptcha';

import {routes} from './app.routes';
import {provideHttpClient} from '@angular/common/http';

// reCAPTCHA v3 Site Key
const RECAPTCHA_SITE_KEY = '6LfIkmwsAAAAABHM4iYcWsi8Jdgz3c34ohTB0k7Q';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),

    importProvidersFrom(RecaptchaV3Module),
    {
      provide: RECAPTCHA_V3_SITE_KEY,
      useValue: RECAPTCHA_SITE_KEY
    }
  ]
};
