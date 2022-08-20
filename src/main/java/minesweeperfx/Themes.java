package minesweeperfx;

import java.io.File;

public enum Themes {
//    DarkTheme("themes/DarkTheme.css", "Dark Theme"), LightTheme("themes/LightTheme.css", "Light Theme");
    DarkTheme("themes/DarkTheme.css", "Dark Theme"), LightTheme("themes/LightTheme.css", "Light Theme");
    
    private final String themeURL;
    private final String themeName;
    
    Themes(String themeCSSFile, String themeName) {
//        System.out.println(themeCSSFile);

        themeURL = ClassLoader.getSystemResource(themeCSSFile).toExternalForm();

        this.themeName = themeName;
    }

    public String getThemeURL() {
        return themeURL;
    }

    public String getThemeName() {
        return themeName;
    }
    
    @Override
    public String toString() {
        return themeName;
    }
}
