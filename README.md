# المُصَلِّي — Web/PWA
تطبيق عربي لمواقيت الصلاة مع واجهة مكالمة تنبيه وإعدادات.

## رفعه إلى GitHub Pages
1. أنشئ Repository باسم `AlMusalli`.
2. ارفع **كل محتويات المجلد** إلى جذر المستودع (تأكد أن `index.html` في الجذر).
3. Settings → Pages → Deploy from a branch → `main` → `/ (root)` → Save.
4. افتح رابط GitHub Pages عبر HTTPS، ثم اضغط تحديد موقعي.

## الملفات
- `index.html`: هيكل الواجهة
- `style.css`: التصميم
- `app.js`: المواقيت والتوقيت والاتصال
- `manifest.webmanifest`: إعداد PWA
- `sw.js`: Service Worker
- `assets/icon.svg`: أيقونة

## تنبيه تقني
المواقيت من Aladhan API وتحتاج إنترنت. الموقع يحتاج HTTPS.
التنبيه الصوتي داخل الصفحة يتطلب إبقاء الصفحة نشطة، وقد يوقفه المتصفح بالخلفية. الويب لا يضمن مكالمة بملء الشاشة عند إغلاق الصفحة أو قفل الهاتف؛ ذلك يتطلب Android native.