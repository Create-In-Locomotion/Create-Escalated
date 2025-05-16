package rbasamoyai.escalated.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import rbasamoyai.escalated.config.EscalatedConfigs;

public class EscalatedModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return EscalatedConfigs::createConfigScreen;
    }
}
