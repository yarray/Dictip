Dictip
======

Android dictionary that displays translation results of copied words as tips.

Download apk : [https://drive.google.com/file/d/0B0hmTVv8yaSeMF85NWI2aUVPS2M/edit?usp=sharing](https://drive.google.com/file/d/0B0hmTVv8yaSeMF85NWI2aUVPS2M/edit?usp=sharing)

The project uses:

* [stardict reading library](https://code.google.com/p/open-ones/source/browse/trunk/ProjectList/InnoDict/Stardict-Core/?r=406) from openones,
* the [porter stemming algorithm](http://www.tartarus.org/~martin/PorterStemmer) from M.F.Porter. and
* the [inflector](https://github.com/flextao/inflector) from flextao.

The embedded library is langdao English-Chinese dictionary under GPL license. As I know this use case could refer to the "aggregation" cases rather than the "redistribution" case, so there should be no problem.

Usage:

In main window press central button to turn translating on/off.
When the translating is on, simply copy any word and its translation will pop up.
Stardict compatible dictionaries are supported. Download them and extract the .dict(or .dict.dz), .ifo and .idx files to "Android/io.github.yarray.dictip/files/" under the sdcard root, then you can select your dictionaries from menu -> select dictionary.

*Advanced usage*

I exposed a broadcast receiver to enable other apps (Tasker, for example) sending intent to control the app, the intent should be like this:

Action: io.github.yarray.dictip.TOGGLE
Extras:
ON: boolean
DICT\_NAME: String (optional)

Note that the intent will take effect "on-the-fly", that is, the status will not be persisted and will fallback to the settings made in the main window.


**Chinese Version**

Dictip 是安卓下的词典软件，可以在复制单词的时候在屏幕上闪出翻译。

下载安装包 ：http://pan.baidu.com/s/1bngUAU7

本项目使用：

* openones 开发的[查询stardict的库]( https://code.google.com/p/open-ones/source/browse/trunk/ProjectList/InnoDict/Stardict-Core/?r=406)和
* M.F.Porter 开发的[词干分析器](http://www.tartarus.org/~martin/PorterStemmer)
* flextao 开发的[词性变化器](https://github.com/flextao/inflector)

内置词典为朗道英汉词典，该词典使用GPL协议发布。据我所知此情形可以适用“组装”而非“再发布”的条款，应不具版权问题。

使用方法：

在主界面点击中央按钮来打开/关闭翻译功能。
当翻译功能处于打开状态，复制任何词到剪贴板，都会弹出翻译
本软件支持 Stardict 兼容词典，下载此类词典后，解压 .dict (或者 .dict.dz )，.ifo 和 .idx 文件到sd卡目录下的"Android/io.github.yarray.dictip/files/"目录下，然后可以在主界面的菜单-> select dictionary 选择添加的词典。

*高级使用方法：*

本软件开放了一个 broadcast receiver 以便第三方软件控制 (如Tasker)，intent 为如下形式：

Action: io.github.yarray.dictip.TOGGLE
Extras:
ON: boolean
DICT\_NAME: String (optional)

注意此intent只是临时改变状态，即其设置不会持久化，当其关闭翻译，服务状态会退回在主界面全局设置的情况。
