/*   _______ __ __                    _______                    __
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 *
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav "Gussoh" Sohtell
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package silvertrout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Classloader for plugins.
 *
 * Each plugin will have its own unique classloader that will help it load
 * itself, any subclass and its common classes. This allows plugins to be
 * unloaded and loaded at run time.
 *
 * @author  Gustav Tiger
 */
class PluginClassLoader extends ClassLoader {

    /**
     * Find a class by name.
     *
     * This function converts the class name into a file name and tries to
     * find and load that file as a class. This only looks for classes in the
     * packages silvertrout.plugins or silvertrout.commons. All other classes
     * will make this function throw a ClassNotFoundException.
     *
     * @param  name  The binary name of the class, starting with
     *               silvertrout.plugins or silvertrout.commons
     * @return       The class
     *
     * @throws ClassNotFoundException If the class name is invalid or the file
     *                                could not be found
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("silvertrout.plugins")
                || name.startsWith("silvertrout.commons")) {
            name = name.replace('.', '/');
            try {
                URL  u = super.getResource(name + ".class");
                if(u == null) {
                    throw new ClassNotFoundException(
                            "Class " + name + ".class does not exists");
                }
                File f = new File(u.toURI());
                if (f.exists()) {
                    byte[] bytes = new byte[(int) f.length()];
                    FileInputStream s = new FileInputStream(f);
                    for (int i = 0; i < f.length(); i++) {
                        bytes[i] = (byte) s.read();
                    }
                    return defineClass(null, bytes, 0, (int) f.length());
                } else {
                    throw new ClassNotFoundException(
                            "Class " + name + ".class does not exists");
                }
            } catch (FileNotFoundException e) {
                throw new ClassNotFoundException(
                        "Class " + name + ".class does not exists");
            } catch (IOException e) {
                throw new ClassNotFoundException(
                        "There was an error reading " + name + ".class");
            } catch (ClassFormatError e) {
                throw new ClassNotFoundException(
                        "Class " + name + ".class is not a valid class");
            }catch(URISyntaxException e) {
                throw new ClassNotFoundException(
                        "String " + name + ".class could not be parsed as a"
                        + " URI reference");
            }
        } else {
            throw new ClassNotFoundException(
                    "Class " + name + " have a non-valid prefix");
        }

    }

    /**
     * Load a class by name.
     *
     * This function tries to load a class. First it looks at the name. If the
     * name starts with silvertrout.plugins or silvertrout.commons, the function
     * will call findClass to make sure that these classes is unique to the
     * plugin. Any other plugin can be shared and is located using the parent
     * classloader.
     *
     * @param  name                   The binary name of the class
     * @return                        The class
     * @throws ClassNotFoundException If the class could not be found
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("silvertrout.plugins") || name.startsWith("silvertrout.commons")) {
            return findClass(name);
        } else {
            return super.loadClass(name, resolve);
        }
    }
}

