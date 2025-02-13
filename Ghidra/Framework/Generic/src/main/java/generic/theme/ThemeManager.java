/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package generic.theme;

import java.awt.*;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

import generic.theme.builtin.*;
import ghidra.framework.OperatingSystem;
import ghidra.framework.Platform;
import ghidra.util.Msg;
import ghidra.util.datastruct.WeakDataStructureFactory;
import ghidra.util.datastruct.WeakSet;
import resources.ResourceManager;
import utilities.util.reflection.ReflectionUtilities;

/**
 * This class manages application themes and their values. The ThemeManager is an abstract
 * base class that has two concrete subclasses (and others for testing purposes) -
 * StubThemeManager and ApplicationThememManager. The StubThemeManager exists as a placeholder
 * until the ApplicationThemeManager is installed via {@link ApplicationThemeManager#initialize()}.
 * <P>
 * The basic idea is that all the colors, fonts, and icons used in an application should be
 * accessed indirectly via an "id" string. Then the actual color, font, or icon can be changed
 * without changing the source code. The default mapping of the id strings to a value is defined
 * in <name>.theme.properties files which are dynamically discovered by searching the module's
 * data directory. Also, these files can optionally define a dark default value for an id which
 * would replace the standard default value in the event that the current theme specifies that it
 * is a dark theme. Themes are used to specify the application's {@link LookAndFeel}, whether or
 * not it is dark, and any customized values for colors, fonts, or icons. There are several
 * "built-in" themes, one for each supported {@link LookAndFeel}, but additional themes can
 * be defined and stored in the users application home directory as a <name>.theme file.
 * <P>
 * Clients that just need to access the colors, fonts, and icons from the theme can use the
 * convenience methods in the {@link Gui} class.  Clients that need to directly manipulate the
 * themes and values will need to directly use the ThemeManager which and be retrieved using the
 * static {@link #getInstance()} method.
 */

public abstract class ThemeManager {

	public static final String THEME_DIR = "themes";

	static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
	static final Color DEFAULT_COLOR = Color.CYAN;

	protected static ThemeManager INSTANCE;

	protected GTheme activeTheme = getDefaultTheme();

	protected GThemeValueMap javaDefaults = new GThemeValueMap();
	protected GThemeValueMap systemValues = new GThemeValueMap();
	protected GThemeValueMap currentValues = new GThemeValueMap();

	protected GThemeValueMap applicationDefaults = new GThemeValueMap();
	protected GThemeValueMap applicationDarkDefaults = new GThemeValueMap();

	// these notifications are only when the user is manipulating theme values, so rare and at
	// user speed, so using copy on read
	private WeakSet<ThemeListener> themeListeners =
		WeakDataStructureFactory.createCopyOnReadWeakSet();

	public static ThemeManager getInstance() {
		return INSTANCE;
	}

	public ThemeManager() {
		if (INSTANCE == null) {
			// default behavior is only install to INSTANCE if first time
			INSTANCE = this;
		}
	}

	protected void installInGui() {
		Gui.setThemeManager(this);
	}

	protected void loadDefaultThemeValues() {
		ThemeDefaultsProvider provider = new ThemeDefaultsProvider();
		applicationDefaults = provider.getDefaults();
		applicationDarkDefaults = provider.getDarkDefaults();
	}

	protected void buildCurrentValues() {
		GThemeValueMap map = new GThemeValueMap();

		map.load(javaDefaults);
		map.load(systemValues);
		map.load(applicationDefaults);
		if (activeTheme.useDarkDefaults()) {
			map.load(applicationDarkDefaults);
		}
		map.load(activeTheme);
		currentValues = map;
	}

	/**
	 * Reloads the defaults from all the discoverable theme.property files.
	 */
	public void reloadApplicationDefaults() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Restores all the current application back to the values as specified by the active theme.
	 * In other words, reverts any changes to the active theme that haven't been saved.
	 */
	public void restoreThemeValues() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Restores the current color value for the given color id to the value established by the
	 * current theme.
	 * @param id the color id to restore back to the original theme value
	 */
	public void restoreColor(String id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Restores the current font value for the given font id to the value established by the
	 * current theme.
	 * @param id the font id to restore back to the original theme value
	 */
	public void restoreFont(String id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Restores the current icon value for the given icon id to the value established by the
	 * current theme.
	 * @param id the icon id to restore back to the original theme value
	 */
	public void restoreIcon(String id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns true if the color associated with the given id has been changed from the current
	 * theme value for that id.
	 * @param id the color id to check if it has been changed
	 * @return true if the color associated with the given id has been changed from the current
	 * theme value for that id.
	 */
	public boolean isChangedColor(String id) {
		return false;
	}

	/**
	 * Returns true if the font associated with the given id has been changed from the current
	 * theme value for that id.
	 * @param id the font id to check if it has been changed
	 * @return true if the font associated with the given id has been changed from the current
	 * theme value for that id.
	 */
	public boolean isChangedFont(String id) {
		return false;
	}

	/**
	 * Returns true if the Icon associated with the given id has been changed from the current
	 * theme value for that id.
	 * @param id the Icon id to check if it has been changed
	 * @return true if the Icon associated with the given id has been changed from the current
	 * theme value for that id.
	 */
	public boolean isChangedIcon(String id) {
		return false;
	}

	/**
	 * Sets the application's active theme to the given theme.
	 * @param theme the theme to make active
	 */
	public void setTheme(GTheme theme) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds the given theme to set of all themes.
	 * @param newTheme the theme to add
	 */
	public void addTheme(GTheme newTheme) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes the theme from the set of all themes. Also, if the theme has an associated
	 * file, the file will be deleted.
	 * @param theme the theme to delete
	 */
	public void deleteTheme(GTheme theme) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a set of all known themes.
	 * @return a set of all known themes.
	 */
	public Set<GTheme> getAllThemes() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a set of all known themes that are supported on the current platform.
	 * @return a set of all known themes that are supported on the current platform.
	 */
	public Set<GTheme> getSupportedThemes() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the active theme.
	 * @return the active theme.
	 */
	public GTheme getActiveTheme() {
		return activeTheme;
	}

	/**
	 * Returns the {@link LafType} for the currently active {@link LookAndFeel}
	 * @return the {@link LafType} for the currently active {@link LookAndFeel}
	 */
	public LafType getLookAndFeelType() {
		return activeTheme.getLookAndFeelType();
	}

	/**
	 * Returns the known theme that has the given name.
	 * @param themeName the name of the theme to retrieve
	 * @return the known theme that has the given name
	 */
	public GTheme getTheme(String themeName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a {@link GThemeValueMap} of all current theme values including unsaved changes to the
	 * theme.
	 * @return a {@link GThemeValueMap} of all current theme values
	 */
	public GThemeValueMap getCurrentValues() {
		return new GThemeValueMap(currentValues);
	}

	/**
	 * Returns the theme values as defined by the current theme, ignoring any unsaved changes that
	 * are currently applied to the application.
	 * @return the theme values as defined by the current theme, ignoring any unsaved changes that
	 * are currently applied to the application
	 */
	public GThemeValueMap getThemeValues() {
		GThemeValueMap map = new GThemeValueMap();
		map.load(javaDefaults);
		map.load(systemValues);
		map.load(applicationDefaults);
		if (activeTheme.useDarkDefaults()) {
			map.load(applicationDarkDefaults);
		}
		map.load(activeTheme);
		return map;
	}

	/**
	 * Returns a {@link GThemeValueMap} contains all values that differ from the default
	 * values (values defined by the {@link LookAndFeel} or in the theme.properties files.
	 * @return a {@link GThemeValueMap} contains all values that differ from the defaults.
	 */
	public GThemeValueMap getNonDefaultValues() {
		return currentValues.getChangedValues(getDefaults());
	}

	/**
	 * Returns the {@link Color} registered for the given id. Will output an error message if
	 * the id can't be resolved.
	 * @param id the id to get the direct color for
	 * @return the {@link Color} registered for the given id.
	 */
	public Color getColor(String id) {
		ColorValue color = currentValues.getColor(id);

		if (color == null) {
			error("No color value registered for: '" + id + "'");
			return DEFAULT_COLOR;
		}
		return color.get(currentValues);
	}

	/**
	 * Returns the current {@link Font} associated with the given id. A default font will be
	 * returned if the font can't be resolved and an error message will be printed to the console.
	 * @param id the id for the desired font
	 * @return the current {@link Font} associated with the given id.
	 */
	public Font getFont(String id) {
		FontValue font = currentValues.getFont(id);

		if (font == null) {
			error("No color value registered for: '" + id + "'");
			return DEFAULT_FONT;
		}
		return font.get(currentValues);
	}

	/**
	 * Returns the Icon registered for the given id. If no icon is registered for the id,
	 * the default icon will be returned and an error message will be dumped to the console
	 * @param id the id to get the registered icon for
	 * @return the actual icon registered for the given id
	 */
	public Icon getIcon(String id) {
		IconValue icon = currentValues.getIcon(id);
		if (icon == null) {
			error("No icon value registered for: '" + id + "'");
			return ResourceManager.getDefaultIcon();
		}
		return icon.get(currentValues);
	}

	/**
	 * Updates the current font for the given id.
	 * @param id the font id to update to the new color
	 * @param font the new font for the id
	 */
	public void setFont(String id, Font font) {
		setFont(new FontValue(id, font));
	}

	/**
	 * Updates the current value for the font id in the newValue
	 * @param newValue the new {@link FontValue} to install in the current values.
	 */
	public void setFont(FontValue newValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Updates the current color for the given id.
	 * @param id the color id to update to the new color
	 * @param color the new color for the id
	 */
	public void setColor(String id, Color color) {
		if (color == null) {
			throw new IllegalArgumentException("Can't set theme value to null!");
		}
		if (color instanceof GColor gColor) {
			if (id.equals(gColor.getId())) {
				Throwable t = new Throwable();
				Msg.error(this, "Attempted to set a color for id \"" + id + "\" using a GColor" +
					" defined using that same id! This would create a self reference!", t);
				return;  // this would create a circular reference to itself, don't do it
			}
		}
		setColor(new ColorValue(id, color));
	}

	/**
	 * Updates the current value for the color id in the newValue
	 * @param newValue the new {@link ColorValue} to install in the current values.
	 */
	public void setColor(ColorValue newValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Updates the current {@link Icon} for the given id.
	 * @param id the icon id to update to the new icon
	 * @param icon the new {@link Icon} for the id
	 */
	public void setIcon(String id, Icon icon) {
		setIcon(new IconValue(id, icon));
	}

	/**
	 * Updates the current value for the {@link Icon} id in the newValue
	 * @param newValue the new {@link IconValue} to install in the current values.
	 */
	public void setIcon(IconValue newValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the {@link GThemeValueMap} containing all the default theme values defined by the
	 * current {@link LookAndFeel}.
	 * @return  the {@link GThemeValueMap} containing all the default theme values defined by the
	 * current {@link LookAndFeel}
	 */
	public GThemeValueMap getJavaDefaults() {
		GThemeValueMap map = new GThemeValueMap();
		map.load(javaDefaults);
		return map;
	}

	/**
	 * Returns the {@link GThemeValueMap} containing all the dark default values defined
	 * in theme.properties files. Note that dark defaults includes light defaults that haven't
	 * been overridden by a dark default with the same id.
	 * @return the {@link GThemeValueMap} containing all the dark values defined in
	 * theme.properties files
	 */
	public GThemeValueMap getApplicationDarkDefaults() {
		GThemeValueMap map = new GThemeValueMap(applicationDefaults);
		map.load(applicationDarkDefaults);
		return map;
	}

	/**
	 * Returns the {@link GThemeValueMap} containing all the standard default values defined
	 * in theme.properties files.
	 * @return the {@link GThemeValueMap} containing all the standard values defined in
	 * theme.properties files
	 */
	public GThemeValueMap getApplicationLightDefaults() {
		GThemeValueMap map = new GThemeValueMap(applicationDefaults);
		return map;
	}

	/**
	 * Returns a {@link GThemeValueMap} containing all default values for the current theme. It
	 * is a combination of application defined defaults and java {@link LookAndFeel} defaults.
	 * @return the current set of defaults.
	 */
	public GThemeValueMap getDefaults() {
		GThemeValueMap currentDefaults = new GThemeValueMap(javaDefaults);
		currentDefaults.load(systemValues);
		currentDefaults.load(applicationDefaults);
		if (activeTheme.useDarkDefaults()) {
			currentDefaults.load(applicationDarkDefaults);
		}
		return currentDefaults;
	}

	/**
	 * Returns true if the given UI object is using the Aqua Look and Feel.
	 * @param UI the UI to examine.
	 * @return true if the UI is using Aqua
	 */
	public boolean isUsingAquaUI(ComponentUI UI) {
		return activeTheme.getLookAndFeelType() == LafType.MAC;
	}

	/**
	 * Returns true if 'Nimbus' is the current Look and Feel
	 * @return true if 'Nimbus' is the current Look and Feel
	 */
	public boolean isUsingNimbusUI() {
		return activeTheme.getLookAndFeelType() == LafType.NIMBUS;
	}

	/**
	 * Adds a {@link ThemeListener} to be notified of theme changes.
	 * @param listener the listener to be notified
	 */
	public void addThemeListener(ThemeListener listener) {
		themeListeners.add(listener);
	}

	/**
	 * Removes the given {@link ThemeListener} from the list of listeners to be notified of
	 * theme changes.
	 * @param listener the listener to be removed
	 */
	public void removeThemeListener(ThemeListener listener) {
		themeListeners.remove(listener);
	}

	/**
	 * Returns true if there are any unsaved changes to the current theme.
	 * @return true if there are any unsaved changes to the current theme.
	 */
	public boolean hasThemeChanges() {
		return false;
	}

	/**
	 * Returns true if an color for the given Id has been defined
	 * @param id the id to check for an existing color.
	 * @return true if an color for the given Id has been defined
	 */
	public boolean hasColor(String id) {
		return currentValues.containsColor(id);
	}

	/**
	 * Returns true if an font for the given Id has been defined
	 * @param id the id to check for an existing font.
	 * @return true if an font for the given Id has been defined
	 */
	public boolean hasFont(String id) {
		return currentValues.containsFont(id);
	}

	/**
	 * Returns true if an icon for the given Id has been defined
	 * @param id the id to check for an existing icon.
	 * @return true if an icon for the given Id has been defined
	 */
	public boolean hasIcon(String id) {
		return currentValues.containsIcon(id);
	}

	/**
	 * Binds the component to the font identified by the given font id. Whenever the font for
	 * the font id changes, the component will updated with the new font.
	 * @param component the component to set/update the font
	 * @param fontId the id of the font to register with the given component
	 */
	public void registerFont(Component component, String fontId) {
		// do nothing
	}

	/**
	 * Returns true if the current theme use dark default values.
	 * @return true if the current theme use dark default values.
	 */
	public boolean isDarkTheme() {
		return activeTheme.useDarkDefaults();
	}

	/**
	 * Returns the default theme for the current platform.
	 * @return the default theme for the current platform.
	 */
	public static GTheme getDefaultTheme() {
		OperatingSystem OS = Platform.CURRENT_PLATFORM.getOperatingSystem();
		switch (OS) {
			case MAC_OS_X:
				return new MacTheme();
			case WINDOWS:
				return new WindowsTheme();
			case LINUX:
			case UNSUPPORTED:
			default:
				return new NimbusTheme();
		}
	}

	protected void notifyThemeChanged(ThemeEvent event) {
		for (ThemeListener listener : themeListeners) {
			listener.themeChanged(event);
		}
	}

	protected void error(String message) {
		Throwable t = ReflectionUtilities.createThrowableWithStackOlderThan();
		StackTraceElement[] trace = t.getStackTrace();
		StackTraceElement[] filtered = ReflectionUtilities.filterStackTrace(trace, "java.",
			"theme.Gui", "theme.ThemeManager", "theme.GColor");
		t.setStackTrace(filtered);
		Msg.error(this, message, t);
	}

}
