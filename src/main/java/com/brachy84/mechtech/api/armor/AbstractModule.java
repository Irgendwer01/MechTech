package com.brachy84.mechtech.api.armor;

public abstract class AbstractModule implements IModule {

    private final String id;

    protected AbstractModule(String id) {
        this.id = id;
    }

    @Override
    public String getModuleId() {
        return id;
    }
}
