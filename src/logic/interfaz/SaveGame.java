package logic.interfaz;
// permite guardar la configuracion del menu en un archivo

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SaveGame {
    
    // tiene una estructura de guardado de datos
    private Properties data = new Properties();
    
    public boolean cargarData(String path) {
        // tratara de leer el archivo de configuracion
        try (FileInputStream input = new FileInputStream(path)) {
            this.data.load(input);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public String getData(String key, String defecto) {
        return this.data.getProperty(key, defecto);
    }
    
    public void setData(String key, String valor) {
        this.data.setProperty(key, valor);
    }
    
    public boolean guardarData(String path) {
        // tratara de guardar el archivo de configuracion
        try (FileOutputStream out = new FileOutputStream(path)) {
            this.data.store(out, "Adsobalin Configuracion");
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
