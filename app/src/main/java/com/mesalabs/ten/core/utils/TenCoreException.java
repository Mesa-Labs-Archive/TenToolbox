package com.mesalabs.ten.core.utils;

/*
 * 십 Toolbox
 *
 * Coded by BlackMesa123 @2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * ULTRA-MEGA-PRIVATE SOURCE CODE. SHARING TO DEVKINGS TEAM
 * EXTERNALS IS PROHIBITED AND WILL BE PUNISHED WITH ANAL ABUSE.
 */

public class TenCoreException extends RuntimeException {
    public TenCoreException() {
        super();
    }

    public TenCoreException(String message) {
        super(message);
    }

    public TenCoreException(String message, Throwable tr) {
        super(message, tr);
    }

    public TenCoreException(Throwable tr) {
        super(tr);
    }
}
