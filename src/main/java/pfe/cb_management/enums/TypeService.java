package pfe.cb_management.enums;

public enum TypeService {

    // ── Soins – Soin Visage ────────────────────────────────────────────
    SOIN_VISAGE_BASIQUE(Specialite.SOINS, 75),
    SOIN_VISAGE_BASIQUE_FILORGA(Specialite.SOINS, 60),
    SOIN_VISAGE_BASIQUE_MARIA_GALLAND(Specialite.SOINS, 60),
    SOIN_VISAGE_BASIQUE_CAUDALIE(Specialite.SOINS, 60),
    SOIN_VISAGE_SPECIFIQUE(Specialite.SOINS, 90),
    SOIN_VISAGE_SPECIFIQUE_FILORGA(Specialite.SOINS, 90),
    SOIN_VISAGE_SPECIFIQUE_MARIA_GALLAND(Specialite.SOINS, 90),
    SOIN_VISAGE_VIP_MARIA_GALLAND(Specialite.SOINS, 90),
    SOIN_VISAGE_SPECIFIQUE_CAUDALIE(Specialite.SOINS, 90),

    // ── Soins – Soin Pied ──────────────────────────────────────────────
    SOIN_PIED_BASIQUE(Specialite.SOINS, 60),
    SOIN_PIED_SPECIFIQUE(Specialite.SOINS, 90),

    // ── Soins – Soin Main ──────────────────────────────────────────────
    SOIN_MAIN_BASIQUE(Specialite.SOINS, 60),
    SOIN_MAIN_SPECIFIQUE(Specialite.SOINS, 90),

    // ── Soins – Hydrafacial ────────────────────────────────────────────
    HYDRAFACIAL_NORMAL(Specialite.SOINS, 60),
    HYDRAFACIAL_MARIA_GALLAND(Specialite.SOINS, 90),

    // ── Soins – Head Spa ───────────────────────────────────────────────
    HEAD_SPA_ESSENTIEL(Specialite.SOINS, 60),
    HEAD_SPA_RELAX(Specialite.SOINS, 60),
    HEAD_SPA_SIGNATURE(Specialite.SOINS, 90),

    // ── Coiffure ──────────────────────────────────────────────────────
    COUPE(Specialite.COIFFEUSE, 60),
    PROTEINE_CHEVEUX_COURT(Specialite.COIFFEUSE, 150),
    PROTEINE_CHEVEUX_MI_LONG(Specialite.COIFFEUSE, 180),
    PROTEINE_CHEVEUX_LONG(Specialite.COIFFEUSE, 240),
    PROTEINE_CHEVEUX_TRES_LONG(Specialite.COIFFEUSE, 240),
    COLORATION_SIMPLE(Specialite.COIFFEUSE, 120),
    BALAYAGE(Specialite.COIFFEUSE, 240),
    SOIN_CAPILLAIRE_CLASSIQUE(Specialite.COIFFEUSE, 120),
    SOIN_CAPILLAIRE_PREMIUM(Specialite.COIFFEUSE, 120),
    BRUSHING(Specialite.COIFFEUSE, 30),
    COIFFURE_EQUIPE(Specialite.COIFFEUSE, 60),
    COIFFURE_SPECIALISTE(Specialite.COIFFEUSE, 90),

    // ── Esthéticienne ─────────────────────────────────────────────────
    EPILATION_VISAGE(Specialite.ESTHETICIENNE, 15),
    EPILATION_MOUSTACHE(Specialite.ESTHETICIENNE, 15),
    EPILATION_SOURCILS(Specialite.ESTHETICIENNE, 15),

    // ── Onglerie ──────────────────────────────────────────────────────
    VERNIS_PERMANENT_MAIN(Specialite.ONGLERIE, 60),
    VERNIS_PERMANENT_PIEDS(Specialite.ONGLERIE, 60),
    GEL_SUR_ONGLE_NATURELLE(Specialite.ONGLERIE, 120),
    GEL_CAPSULE(Specialite.ONGLERIE, 120),
    BABY_BOOMER(Specialite.ONGLERIE, 120),

    // ── Maquillage ────────────────────────────────────────────────────
    MAQUILLAGE_PAR_EQUIPE(Specialite.MAQUILLEUSE, 90),

    // ── Maquillage Mariée ─────────────────────────────────────────────
    MAQUILLAGE_SDAG(Specialite.MAQUILLEUSE, 120),
    MAQUILLAGE_HENNA(Specialite.MAQUILLEUSE, 120),
    MAQUILLAGE_BADOU(Specialite.MAQUILLEUSE, 90),
    MAQUILLAGE_D5OUL(Specialite.MAQUILLEUSE, 120),
    MAQUILLAGE_FIANCAILLES(Specialite.MAQUILLEUSE, 90);

    private final Specialite specialite;
    private final int dureeMinutes;

    TypeService(Specialite specialite, int dureeMinutes) {
        this.specialite = specialite;
        this.dureeMinutes = dureeMinutes;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }
}
