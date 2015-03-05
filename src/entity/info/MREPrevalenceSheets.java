package entity.info;

/**
 * Created by tloehr on 05.03.15.
 */
public class MREPrevalenceSheets {

    public static final int PRESENT_YESTERDAY = 10; // resinfo types "stay", "absence"
    public static final int YEAR_OF_BIRTH = 20; // resident "dob" 
    public static final int MALE = 30; // resident "Geschlecht == 1"
    public static final int FEMALE = 40; // resident "Geschlecht == 2"
    public static final int URINE_CATHETER = 50; // resinfotype "INKOAID2", trans.aid=true
    public static final int VESSEL_CATHETER = 60;
    public static final int DECUBITUS = 70; // resinfotype "wound[1..5]"
    public static final int TRACHEOSTOMA = 80; // resinfotype "respi"
    public static final int OTHER_WOUNDS = 90; // resinfotype "wound[1..5]"
    public static final int PEG = 100; // resinfotype "ARTNUTRIT", tubetype=peg
    public static final int MRSA = 110; // resinfotype "INFECT1", mrsa=true


    // wohin mit dem KH Aufenthalt und der OP ?

    
}
