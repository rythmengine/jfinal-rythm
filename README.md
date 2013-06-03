JFinal-Rythm
=============

Enable JFinal user to use Rythm (https://github.com/greenlaw110/rythm) template engine to render views.

Usage:

In your Java source file

1. Load configuration from your properties file

```lang-java
public void configConstant(Constants me) {
    this.conf = loadPropertyFile("path/to/properties/");
}
```

2. Enable Rythm

Config RythmPlugin in your configPlugin method using the configuration loaded in the above step:

```lang-java
public void configPlugin(Plugins me) {
    ...
    me.add(new RythmPlugin(conf));
} 
```

In your properties file:

1. Configure template home in your properties file:

```
# this set the template home to WEB-INF/rythm folder
rythm.home.template=../rythm
```

2. Enable i18n if needed in your properties file:

```
rythm.i18n.enabled=true
```

Check the SAMPLES.txt file to find sample applications
