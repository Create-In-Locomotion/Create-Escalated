package rbasamoyai.escalated.config;

public class EscalatedServerCfg extends EscalatedConfigBase {

    public final ConfigInt maxWalkwayLength = i(128, 3, "maxWalkwayLength", "[in blocks]");
    public final ConfigInt maxEscalatorHeight = i(32, 2, "maxEscalatorHeight", "[in blocks]");
    public final ConfigInt maxWalkwayWidth = i(10, 1, "maxWalkwayWidth", "[in blocks]");
    public final ConfigInt maxEscalatorWidth = i(10, 1, "maxEscalatorWidth", "[in blocks]");

    @Override public String getName() { return "server"; }

}
