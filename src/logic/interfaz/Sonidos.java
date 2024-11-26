package logic.interfaz;
// maneja todos los audios del juego

import javafx.scene.media.AudioClip;
import logic.abstractos.Tools;

public class Sonidos {
    
    // constantes para alcance del sonido
    public static final float DIST_OIDO = 420f * (float)Adsobalin.ESCALA;
    public static final float DIST_BALANCE = 210f * (float)Adsobalin.ESCALA;
    
    // listado de sonidos
    public static final int SND_DISPARO = 0;
    public static final int SND_CURACION = 1;
    public static final int SND_IMPACTO = 2;
    public static final int SND_RECARGA = 3;
    public static final int SND_HIT_ENEMY = 4; // 5,6,7
    public static final int SND_HIT_ALLY = 8; // 9,10,11
    public static final int SND_HIT_PLAYER = 12; // 13,14
    public static final int SND_INI_ENEMY = 15; // 16,17
    public static final int SND_INI_ALLY = 18; // 19,20
    public static final int SND_INI_PLAYER = 21; // 22,23
    public static final int SND_KILL_PLAYER = 24; // 25,26
    private static final AudioClip[] SND = new AudioClip[27];
    
    public Sonidos() {
        SND[SND_DISPARO] = new AudioClip(getClass().getResource(
                "/assets/sonidos2d/disparo.wav").toString());
        SND[SND_CURACION] = new AudioClip(getClass().getResource(
                "/assets/sonidos2d/curacion.wav").toString());
        SND[SND_IMPACTO] = new AudioClip(getClass().getResource(
                "/assets/sonidos2d/impacto.wav").toString());
        SND[SND_RECARGA] = new AudioClip(getClass().getResource(
                "/assets/sonidos2d/recarga.wav").toString());
        for (int i = 0; i < 4; i++) {
            SND[SND_HIT_ENEMY + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/hit_enemigo" + i + ".wav").toString());
            SND[SND_HIT_ALLY + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/hit_guardian" + i + ".wav").toString());
        }
        for (int i = 0; i < 3; i++) {
            SND[SND_HIT_PLAYER + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/hit_player" + i + ".wav").toString());
            SND[SND_INI_ENEMY + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/ini_enemigo" + i + ".wav").toString());
            SND[SND_INI_ALLY + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/ini_guardian" + i + ".wav").toString());
            SND[SND_INI_PLAYER + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/ini_player" + i + ".wav").toString());
            SND[SND_KILL_PLAYER + i] = new AudioClip(getClass().getResource(
                    "/assets/sonidos2d/kill_player" + i + ".wav").toString());
        }
    }
    
    public static void sonidoPos(int indSnd, float[] fuente) {
        float dist = Tools.vecDistancia(fuente, Mundo.camaraCen);
        float vol = Math.max(0f, 1f - (dist / DIST_OIDO));
        if (vol > 0) {
            float bal = Math.max(-1f, Math.min(1f,
                    (fuente[0] - Mundo.camaraCen[0]) / DIST_BALANCE));
            switch (indSnd) {
                case SND_HIT_ENEMY:
                case SND_HIT_ALLY:
                    indSnd += Adsobalin.DADO.nextInt(4);
                    break;
                case SND_HIT_PLAYER:
                case SND_INI_ENEMY:
                case SND_INI_ALLY:
                case SND_INI_PLAYER:
                case SND_KILL_PLAYER:
                    indSnd += Adsobalin.DADO.nextInt(3);
                    break;
            }
            SND[indSnd].setVolume(vol);
            SND[indSnd].setBalance(bal);
            SND[indSnd].play();
        }
    }
}
