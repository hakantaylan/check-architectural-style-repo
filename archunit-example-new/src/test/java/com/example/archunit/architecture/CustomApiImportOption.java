package com.example.archunit.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

public class CustomApiImportOption implements ImportOption {
    @Override
    public boolean includes(Location location) {
        boolean includesTest = Predefined.DO_NOT_INCLUDE_TESTS.includes(location);
        if(includesTest)
            return location.contains("com/example/archunit/ddd");
        else
            return includesTest;
    }
}
