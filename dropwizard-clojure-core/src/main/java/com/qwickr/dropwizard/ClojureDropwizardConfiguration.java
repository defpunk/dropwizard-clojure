package com.qwickr.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class ClojureDropwizardConfiguration extends Configuration {

	@NotNull
    private Map settings;

    @JsonProperty
    public Map getSettings() {
        return settings;
    }

    @JsonProperty
    public void setSettings(Map settings) {
        this.settings = settings;
    }

}