package io.github.darkkronicle.kronhud.gui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.render.Shader;

public class ShaderHandler {

    private final static ShaderHandler INSTANCE = new ShaderHandler();

    public static ShaderHandler getInstance() {
        return INSTANCE;
    }

    private ShaderHandler() {}

    @Getter @Setter
    private Shader chromaColor = null;

}
