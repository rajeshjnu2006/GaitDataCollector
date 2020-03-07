#Edited by @Rajesh Kumar. 
# I customized the following data collection app to collect the data from participants in my study. 
Followig modifications were made to get the app work the way my data collection demanded.

(1) The sampling rate was fixed to 46
(2) The app was modified to make it fault tolerent. In other words, the app was getting to sleep in the higher version of Android platforms randomly after a few seconds. The app was thus modified to get rid of the problem.
(3) Another work-around that I found to the sleeping app issues is to install another app like CPU awake that keeps the phone awake as long as the data is being collected
(4) There was also glitch in saving ans sharing the data stored into files. The code was debugged to make sure that the app allows the researchers to be able to save/share the data (.csv) files. 

#####################################################################################################################################

# Sense-it _(sensors)_ #

The Android app for the [nQuire-it project][nq] — _Young Citizen Inquiry_.

[![Sense-it app - on Google Play][sense-it-icon]][app]

This repository contains the Java source and the built APK files for the [Sense-it app][app] (_aka sciencetoolkit_).

 * <https://github.com/nQuire/sciencetoolkit>
 * <https://github.com/IET-OU/nquire-web-source>

Visit the [nQuire-it web site][nq].

[![Sense-it - on Google Play][img-1]][app] [![Sense-it - on Google Play (2)][img-2]][app]

Do you need a set of sensors to collect data for your project? You may already have it on your Android device!

_Sense-it_ gives access to all the sensors on your smartphone or tablet devices.
If you are curious about the sensors your device may have and what you might do with them,
use this app to record data and generate graphs from your sensors, or check the type and hardware model of your sensors.

[![nQuire video - on YouTube][yt-img-1]][yt]

 * [Twitter: @nQuireit][tw]

## GDPR

Details of GDPR / privacy fixes can be found in [Bug #68][].

[Privacy policy][].

## Licence

_Sense-it_ is released under the GPLv3 licence. See [LICENSE][] for more details.

---

Contributors:  [@evilfier][] (_developer_), [thea][], [mike][].

---
[©][c] 2013-2019 [The Open University][ou] ([Institute of Educational Technology][iet]).

[nq]: http://www.nquire-it.org/#/home "nQuire-it: Young Citizen Inquiry"
[app]: https://play.google.com/store/apps/details?id=org.greengin.sciencetoolkit&hl=en_GB
  "Sense-it (sensors) Android app — on Google Play"
[dev]: https://play.google.com/store/apps/developer?id=nQuire:+Young+Citizen+Inquiry "nQuire-it: Young Citizen Inquiry"
[sense-it-icon]: https://lh5.ggpht.com/SN_LLof2UbhxolOJ6IwnjkOLYLVXTpY3CpIDHzEOBbqPH-xiECx26XftvRmlgqvRl2Q=w300-rw
[1st commit]: https://github.com/nQuire/sciencetoolkit/commit/8801b35381843670 "22-October-2013"

[ou]: https://www.open.ac.uk/
[iet]: https://iet.open.ac.uk/
[c]: https://www.open.ac.uk/copyright "Copyright © 2013-2019 The Open University (IET)."
[LICENSE]: https://github.com/IET-OU/nquire-web-source/blob/1.2-branch/LICENSE.txt
    "GNU General Public License 3.0 onwards [GPL-3.0+]"
[gpl-icon]: https://img.shields.io/badge/license-GLP--3.0%2B-blue.svg
[@evilfier]: https://github.com/evilfer "Eloy Villasclaras Fernandez"
[thea]: https://iet.open.ac.uk/profiles/christothea.herodotou "Dr Christothea Herodotou"
[mike]: https://iet.open.ac.uk/people/mike.sharples "Prof Mike Sharples"

[privacy policy]: http://www.nquire-it.org/#/privacy#sense-it "Privacy policy — on www.nQuire-it.org"
[priv-legacy]: http://www.nquire-it.org/media/nquire-it-privacy-policy.pdf
[tw]: https://twitter.com/nQuireit
[tw-legacy]: https://twitter.com/nquireYCI
[gs-legacy]: https://sites.google.com/site/nquiresensorkit/
[wp-legacy]: https://nquire.wordpress.com/

[yt]: https://youtu.be/w1M9xtkhYEQ "nQuire-it: Young Citizen Inquiry — on YouTube"
[yt-img]: https://i.ytimg.com/vi/w1M9xtkhYEQ/hqdefault.jpg?#
[yt-img-1]: https://i.ytimg.com/vi/w1M9xtkhYEQ/1.jpg
[img-1]: https://lh3.ggpht.com/B8B_hzfIp_0uyVBSbqtUO-OENFbnVfwVMGAL8B0vbyEDA3SYK-6FywuoR1KuvKhjFw=w720-h310-rw
[img-2]: https://lh3.ggpht.com/_AB4efqCZE9oZTflJb13OOo177oik-PrTA7NXP5m0CeSlTN11wvzj8YDrKUg0DloJg=w720-h310-rw

[bug #68]: https://github.com/IET-OU/nquire-web-source/issues/68 "GDPR/privacy"
[End]: //.
