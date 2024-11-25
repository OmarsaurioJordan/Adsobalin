package logic.interfaz;
// maneja todos los audios del juego

import javafx.scene.media.AudioClip;
import logic.abstractos.Tools;

/*
dependencies {
    implementation 'org.openjfx:javafx-controls:17'
    implementation 'org.openjfx:javafx-media:17'
}
*/

public class Sonidos {
    
    // constantes para alcance del sonido
    public static final float DIST_OIDO = 300f * (float)Adsobalin.ESCALA;
    public static final float DIST_BALANCE = 200f * (float)Adsobalin.ESCALA;
    
    // listado de sonidos
    public static final int SND_DISPARO = 0;
    private static final AudioClip[] SND = new AudioClip[1];
    
    public Sonidos() {
        //String p = getClass().getResource("/assets/sonidos/disparo.wav").toString();
        //System.out.println(p);
        //AudioClip a = new AudioClip(p);
        //AudioClip a = new AudioClip("file://D:/Documentos/SonidosEspeciales/sonidosplus/botavapor.wav");
        //a.play();
    }
    
    public static void sonidoPos(int indSnd, float[] fuente) {
        /*float dist = Tools.vecDistancia(fuente, Mundo.camaraCen);
        float vol = Math.max(0f, 1f - (dist / DIST_OIDO));
        if (vol > 0) {
            float bal = Math.max(-1f, Math.min(1f,
                    (fuente[0] - Mundo.camaraCen[0]) / DIST_BALANCE));
            SND[indSnd].setVolume(vol);
            SND[indSnd].setBalance(bal);
            SND[indSnd].play();
        }*/
    }
}
