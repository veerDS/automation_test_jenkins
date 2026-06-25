package com.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Attach to any test class to give it a business-friendly display name
 * in the Extent Report. If absent, the listener falls back to the
 * simple class name — so existing classes never break.
 *
 * Usage:
 *   @ModuleInfo(module = "Profile Management Module",
 *               description = "Covers all profile update and headline edit scenarios")
 *   public class NaukriProfileUpdate extends Print { ... }
 */
@Retention(RetentionPolicy.RUNTIME)   // must be RUNTIME — reflection reads it at execution time
@Target(ElementType.TYPE)             // TYPE = class level only
public @interface ModuleInfo {
    String module();                              // business-friendly class display name (required)
    String description() default "";             // optional tooltip / sub-label in report
}
