package me.ikevoodoo.smpcore.config2.test;

import me.ikevoodoo.smpcore.config2.Configuration;

import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        var config = Configuration.createConfiguration(MainConfig.class, new File("testing.yml"));

        config.getSecond().getEliminationConfig().useMinHealth(true);

        System.out.println(config.getSecond().getEliminationConfig().getMinHearts());
    }

}
