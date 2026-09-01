# PrimeWA 🚀

<div align="center">

![PrimeWA Banner](app/src/main/res/mipmap-xxxhdpi/launcher.png)

### The Ultimate Material 3 Enhancement & Privacy Suite for WhatsApp

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%209.0%2B-green.svg)](https://android.com)
[![Framework](https://img.shields.io/badge/Framework-Xposed%20%2F%20LSPosed-purple.svg)](https://github.com/LSPosed/LSPosed)
[![Style](https://img.shields.io/badge/Design-Material%203-emerald.svg)](https://m3.material.io)

<p><strong>PrimeWA</strong> is a feature-rich, high-performance Xposed module built to supercharge WhatsApp &amp; WhatsApp Business with granular privacy controls, seamless call recording, full-quality media sharing, and modern Material 3 customization.</p>

<p><em>Maintained &amp; Developed by <a href="https://github.com/TheOwaisLone">Owais</a> • Forked from WaEnhancer by Dev4Mod &amp; Contributors</em></p>

</div>

---

## ✨ Key Features

### 📞 Advanced Call Recording
* **Direct Organization**: Automatically structures recordings directly into dedicated `WhatsApp` and `WA Business` folders within your selected directory.
* **VoIP Concurrent Capture**: Zero call drops or reconnect glitches—safely captures microphone audio without interrupting WhatsApp's WebRTC audio pipelines.
* **Contact Information**: Auto-resolves and prefixes recording files with contact names, phone numbers, and timestamps.
* **Integrated Recording Hub**: Built-in audio player, sort filters, and multi-selection batch delete/share directly inside the app.

### 🛡️ Privacy & Anti-Revoke
* **Anti-Revoke Messages & Status**: Prevent senders from deleting messages and view status updates after expiration.
* **Freeze Last Seen & Online Masking**: Control your online visibility and freeze your last seen timestamp.
* **Blue Ticks on Reply**: Read incoming messages privately; blue ticks are only sent after you choose to respond.
* **Presence Privacy**: Hide typing indicator and audio recording presence per chat or globally.
* **Ghost Mode**: One-tap toggle to disable all read/delivery confirmations and presence indicators.

### 🎨 Material 3 & UI Customization
* **Material You Dynamic Theming**: Harmonious palette engine supporting dynamic Monet accents, custom color presets, and manual Hex palettes.
* **Header Theme Switcher**: Toggle between System Default, Dark Mode, and Light Mode directly from the top bar.
* **Hide Launcher Icon**: Option to hide PrimeWA from your app drawer while keeping it fully accessible via LSPosed or in-app shortcuts.
* **Stories & Status Styles**: Switch between Instagram-style cards, Facebook stories, or standard WhatsApp tab layouts.
* **Pure Messaging Mode**: Clean home screen without channel recommendations or clutter.

### 🚀 High-Definition Media & Utilities
* **Original Quality Uploads**: Send full-resolution images and 60FPS videos without aggressive compression.
* **Status & View-Once Downloader**: Save view-once media and contact status updates in pristine quality.
* **Custom Storage Picker**: In-app tree explorer with dedicated folder creation.
* **Version Check Bypass**: Stay uninterrupted across WhatsApp updates.

### 🔐 Device Integrity & Keybox Tools
* **Custom Keybox Support**: Load custom `keybox.xml` certificates for Play Integrity attestation.
* **Bootloader Spoofer**: Mask unlocked bootloader states against app integrity checks.

---

## 📥 Installation

1. **Prerequisites**:
   * A rooted Android device running **Android 9.0 (API 28) or higher** (tested up to Android 16).
   * Working Xposed environment (**LSPosed**, **KernelSU**, **APatch**, or **Zygisk-LSPosed**).
2. **Setup**:
   1. Download the latest `PrimeWA` APK from the [Releases](https://github.com/TheOwaisLone/PrimeWA/releases) or [Actions](https://github.com/TheOwaisLone/PrimeWA/actions) tab.
   2. Install the APK on your device.
   3. Open **LSPosed Manager**, enable the **PrimeWA** module, and select **WhatsApp** / **WhatsApp Business** in the scope.
   4. Force stop WhatsApp and launch PrimeWA to configure your preferences.

---

## ⚖️ License & GPL-3.0 Copyleft Compliance

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See the [LICENSE](LICENSE) file for the complete license text.

### Copyleft & Distribution Terms:
* **GPL-3.0 Derivative Work**: This repository is a modified derivative work based on the upstream open-source project *WaEnhancer*. All source code modifications, additions, and enhancements remain strictly open-source under GPL-3.0.
* **Complete Source Code**: Anyone distributing binaries or derivative versions of PrimeWA must provide the full, machine-readable corresponding source code under the GPL-3.0 license.
* **Preservation of Notices**: All original copyright notices, contributor credits, and warranty disclaimers are preserved in source files and documentation.

---

## 👥 Credits & Acknowledgments

We extend our deep gratitude to **Dev4Mod** and all upstream contributors who established the foundation of this codebase:

* **Original Creator & Foundation:** [Dev4Mod](https://github.com/Dev4Mod)
* **Upstream Contributors:** [frknkrc44](https://github.com/frknkrc44), [mubashardev](https://github.com/mubashardev), [masbentoooredoo](https://github.com/masbentoooredoo), [zhongerxll](https://github.com/zhongerxll), [BryanGIG](https://github.com/BryanGIG), [rizqi-developer](https://github.com/rizqi-developer), [pedroborraz](https://github.com/pedroborraz), [ahmedtohamy1](https://github.com/ahmedtohamy1), [mohdafix](https://github.com/mohdafix), [maulana-kurniawan](https://github.com/maulana-kurniawan), [erzachn](https://github.com/erzachn), [cvnertnc](https://github.com/cvnertnc), [rkorossy](https://github.com/rkorossy), [StupidRepo](https://github.com/StupidRepo), [Blank517](https://github.com/Blank517), [astola-studio](https://github.com/astola-studio), [Strange-IPmart](https://github.com/Strange-IPmart).
* **Core Libraries & Tools:**
  * Bootloader Spoofer by [chiteroman](https://github.com/chiteroman)
  * [LSPosed Framework](https://github.com/LSPosed)
  * Bridge Client and Server by [rhunk](https://github.com/rhunk/)

---

## 🛡️ Terms of Use, Privacy Policy & Absolute Liability Waiver

Please read this disclaimer carefully before using PrimeWA:

* **1. Educational & Research Scope**: PrimeWA is an independent open-source project and Xposed module intended strictly for educational, security research, and personal customization purposes.
* **2. Absolute Disclaimer of Liability & Warranty**: This software is provided **"AS IS"** and **"AS AVAILABLE"** without warranties of any kind, either express or implied. Under no circumstances and under no legal theory (contract, tort, negligence, or otherwise) shall the maintainer/developer of PrimeWA (**Owais / TheOwaisLone**), the original author of WaEnhancer (**Dev4Mod**), or any upstream/downstream contributors be held liable or responsible for:
  * **Device Damages**: Any hardware failures, soft-bricks, bootloops, system errors, or device malfunctions.
  * **Data Loss & Corruption**: Any loss, corruption, leakage, or accidental deletion of chats, media files, call recordings, databases, cryptographic keys, or system data.
  * **Third-Party Actions & Compromises**: Any security breaches, data leaks, or unauthorized interference caused by third-party applications, root exploits, malicious modules, or modified environments.
  * **Account Restrictions & Bans**: Any temporary or permanent account restrictions, suspensions, or bans issued by WhatsApp LLC, Meta Platforms, Inc., or affiliated entities.
* **3. Zero Proprietary Assets**: This repository **DOES NOT** contain, host, or distribute any proprietary assets, decrypted code, or binaries belonging to WhatsApp LLC or Meta Platforms, Inc.
* **4. No Modded APK Binaries**: We do not host or distribute modified WhatsApp client binaries. All modifications are injected dynamically into memory via the Xposed framework on the user's local device.
* **5. User Responsibility**: Installing and using this module is done entirely at the end user's own risk and discretion. By using this software, you assume full responsibility for your actions and waive all claims against the developers and contributors.
