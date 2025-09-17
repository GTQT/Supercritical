package supercritical.api.unification;

import gregtech.api.unification.Element;
import lombok.experimental.ExtensionMethod;

import static gregtech.api.unification.Elements.*;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 MeowmelMuku 于 2025 修改。
 * 修改内容：添加靶丸，。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */

@ExtensionMethod(ElementExtension.Handler.class)
public class SCElements {

    public static final Element U233 = add(92, 141, -1, null, "Uranium-233", "U-233", true);
    public static final Element U234 = add(92, 142, -1, null, "Uranium-234", "U-234", true);
    public static final Element U236 = add(92, 144, -1, null, "Uranium-236", "U-236", true);
    public static final Element U239 = add(92, 147, -1, null, "Uranium-239", "U-239", true);

    public static final Element Np235 = add(93, 142, -1, null, "Neptunium-235", "Np-235", true);
    public static final Element Np236 = add(93, 143, -1, null, "Neptunium-236", "Np-236", true);
    public static final Element Np237 = add(93, 144, -1, null, "Neptunium-237", "Np-237", true);
    public static final Element Np239 = add(93, 146, -1, null, "Neptunium-239", "Np-239", true);

    public static final Element Pu238 = add(94, 144, -1, null, "Plutonium-238", "Pu-238", true);
    public static final Element Pu240 = add(94, 146, -1, null, "Plutonium-240", "Pu-240", true);
    public static final Element Pu242 = add(94, 148, -1, null, "Plutonium-242", "Pu-242", true);
    public static final Element Pu244 = add(94, 150, -1, null, "Plutonium-244", "Pu-244", true);

    public static final Element Th229 = add(93, 143, -1, null, "Thorium-229", "Th-229", true);
    public static final Element Th230 = add(93, 144, -1, null, "Thorium-230", "Th-230", true);


    public static final Element Am241 = add(95, 146, -1, null, "Americium-241", "Am-241", true);
    public static final Element Am242 = add(95, 147, -1, null, "Americium-242", "Am-242", true);
    public static final Element Am243 = add(95, 148, -1, null, "Americium-243", "Am-243", true);

    public static final Element Cm243 = add(96, 146, -1, null, "Curium-243", "Cm-243", true);
    public static final Element Cm244 = add(96, 148, -1, null, "Curium-244", "Cm-244", true);
    public static final Element Cm245 = add(96, 149, -1, null, "Curium-245", "Cm-245", true);
    public static final Element Cm246 = add(96, 150, -1, null, "Curium-246", "Cm-246", true);
    public static final Element Cm247 = add(96, 151, -1, null, "Curium-247", "Cm-247", true);


    static {
        U.setHalfLiveSeconds(1.4090285e+17);
        U234.setHalfLiveSeconds(7.7472253e+12);
        U236.setHalfLiveSeconds(7.4046528e+14);
        U238.setHalfLiveSeconds(1.4090285e+17);
        U235.setHalfLiveSeconds(2.2195037e+16);
        U239.setHalfLiveSeconds(1407);

        Np235.setHalfLiveSeconds(34223040);
        Np236.setHalfLiveSeconds(1.33056e+10);
        Np237.setHalfLiveSeconds(6.76801391e+13);
        Np239.setHalfLiveSeconds(66200371);

        Pu.setHalfLiveSeconds(-1);
        Pu238.setHalfLiveSeconds(2765707200d);
        Pu239.setHalfLiveSeconds(760332960000d);
        Pu240.setHalfLiveSeconds(206907696000d);
        Pu241.setHalfLiveSeconds(450649440d);
        Pu242.setHalfLiveSeconds(1.1826e+13);
        Pu244.setHalfLiveSeconds(2.52288e+15);

        U233.setHalfLiveSeconds(1.586e13);
        U234.setHalfLiveSeconds(7.755e12);
        U236.setHalfLiveSeconds(2.342e16);

        Am241.setHalfLiveSeconds(1.363e10);
        Am242.setHalfLiveSeconds(5.7672e4);
        Am243.setHalfLiveSeconds(2.326e11);

        Cm243.setHalfLiveSeconds(9.18e8);
        Cm244.setHalfLiveSeconds(571590600);
        Cm245.setHalfLiveSeconds(2.68e11);
        Cm246.setHalfLiveSeconds(1.503e11);
        Cm247.setHalfLiveSeconds(4.923e14);
    }

}
