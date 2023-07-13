package com.example.archunit.util;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

public class ImportOnlyRelatedClasses implements ImportOption {
    @Override
    public boolean includes(Location location) {
        boolean notIncludeTests = Predefined.DO_NOT_INCLUDE_TESTS.includes(location);
        if (notIncludeTests)
            return location.contains("com/example/archunit");
        else
            return false;
    }
}
