# Shop Hisab — Android APK project

Ei folder ta ekta ready Android project. Ekhane kono coding korte hobe na — shudhu GitHub e upload korle GitHub nije APK banaye dibe.

## APK banabar niyom (phone diyeo kora jay)

1. github.com e ekta free account khulo (jodi na thake).
2. **New repository** → naam `shop-hisab` → **Private** ba Public → Create.
3. Repository page e **Add file → Upload files** → ei folder er sob file/folder upload koro (`app`, `.github`, `build.gradle`, `settings.gradle`, `gradle.properties`).
   - Desktop theke korle puro folder drag korlei hobe.
4. Upload er por **Actions** tab e jao. "Build Shop Hisab APK" nam er ekta run automatic shuru hobe (2-5 minute).
5. Run ta sesh (green tick) hole run er nicher dike **Artifacts → shop-hisab-apk** e tap koro → zip download hobe.
6. Zip khule `app-debug.apk` phone e install koro (Settings e "Install unknown apps" allow korte hobe).

## Kono kichu change korle

- App er hisab er code `app/src/main/assets/index.html` te. Ei ek file update kore abar upload korlei notun APK build hobe.
- App er naam: `app/src/main/res/values/strings.xml`
- Package id: `app/build.gradle` er `applicationId`

## Note

- Ei APK ta "debug" build — nijer phone e install korar jonno puropuri thik. Play Store e dite chaile release build + signing key lagbe.
- Sob data phone er moddhei thake (WebView localStorage). App uninstall korle data muche jabe — tai Report tab theke majhe majhe **Export backup** koro.
