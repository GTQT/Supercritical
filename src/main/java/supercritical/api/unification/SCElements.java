package supercritical.api.unification;

import static gregtech.api.unification.Elements.*;

import gregtech.api.unification.Element;
import gregtech.api.unification.Elements;
/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸，。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
public class SCElements {

    public static final Element U239 = add(92, 147, -1, null, "Uranium-239", "U-239", true);
    public static final Element Np235 = add(93, 142, -1, null, "Neptunium-235", "Np-235", true);
    public static final Element Np236 = add(93, 143, -1, null, "Neptunium-236", "Np-236", true);
    public static final Element Np237 = add(93, 144, -1, null, "Neptunium-237", "Np-237", true);
    public static final Element Np239 = add(93, 146, -1, null, "Neptunium-239", "Np-239", true);
    public static final Element Pu238 = add(94, 144, -1, null, "Plutonium-238", "Pu-238", true);
    public static final Element Pu240 = add(94, 146, -1, null, "Plutonium-240", "Pu-240", true);
    public static final Element Pu242 = add(94, 148, -1, null, "Plutonium-242", "Pu-242", true);
    public static final Element Pu244 = add(94, 150, -1, null, "Plutonium-244", "Pu-244", true);
    //Additions Nuclear stuff, introduced by KeQingSoCute520
    public static final Element Uranium233 = Elements.add(92, 141, -1, null, "Uranium-233", "U-233", true);
    public static final Element Uranium234 = Elements.add(92, 142, -1, null, "Uranium-234", "U-234", true);
    public static final Element Uranium236 = Elements.add(92, 144, -1, null, "Uranium-236", "U-236", true);

    public static final Element Thorium229 = Elements.add(92, 142, -1, null, "Thorium-229", "Th-229", true);
    public static final Element Thorium230 = Elements.add(92, 144, -1, null, "Thorium-230", "Th-230", true);


    public static final Element Americium241 = Elements.add(95, 146, -1, null, "Americium-241", "Am-241", true);
    public static final Element Americium242 = Elements.add(95, 147, -1, null, "Americium-242", "Am-242", true);
    public static final Element Americium243 = Elements.add(95, 148, -1, null, "Americium-243", "Am-243", true);

    public static final Element Curium243 = Elements.add(96, 147, -1, null, "Curium-243", "Cm-243", true);
    public static final Element Curium245 = Elements.add(96, 149, -1, null, "Curium-245", "Cm-245", true);
    public static final Element Curium246 = Elements.add(96, 150, -1, null, "Curium-246", "Cm-246", true);
    public static final Element Curium247 = Elements.add(96, 151, -1, null, "Curium-247", "Cm-247", true);

    static {
        setHalfLiveSeconds(U, 1.4090285e+17);
        setHalfLiveSeconds(U238, 1.4090285e+17);
        setHalfLiveSeconds(U235, 2.2195037e+16);
        setHalfLiveSeconds(U239, 1407);

        setHalfLiveSeconds(Np, -1);
        setHalfLiveSeconds(Np235, -1);
        setHalfLiveSeconds(Np236, -1);
        setHalfLiveSeconds(Np237, -1);
        setHalfLiveSeconds(Np239, -1);

        setHalfLiveSeconds(Pu, -1);
        setHalfLiveSeconds(Pu238, 2765707200d);
        setHalfLiveSeconds(Pu239, 760332960000d);
        setHalfLiveSeconds(Pu240, 206907696000d);
        setHalfLiveSeconds(Pu241, 450649440d);
        setHalfLiveSeconds(Pu242, 1.1826e+13);
        setHalfLiveSeconds(Pu244, 2.52288e+15);

        setHalfLiveSeconds(Uranium233, 1.586e13);
        setHalfLiveSeconds(Uranium234, 7.755e12);
        setHalfLiveSeconds(Uranium236, 2.342e16);

        setHalfLiveSeconds(Americium241, 1.363e10);
        setHalfLiveSeconds(Americium242, 5.7672e4);
        setHalfLiveSeconds(Americium243, 2.326e11);

        setHalfLiveSeconds(Curium243, 9.18e8);
        setHalfLiveSeconds(Curium245, 2.68e11);
        setHalfLiveSeconds(Curium246, 1.503e11);
        setHalfLiveSeconds(Curium247, 4.923e14);
    }

    private static void setHalfLiveSeconds(Element element, double halfLife) {
        ((ElementExtension) element).setHalfLiveSeconds(halfLife);
    }
}
