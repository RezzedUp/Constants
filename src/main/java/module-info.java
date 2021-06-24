/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * Utilities for {@code static final} constants.
 */
module com.rezzedup.util.constants
{
    requires static pl.tlinkowski.annotation.basic;
    
    exports com.rezzedup.util.constants;
    exports com.rezzedup.util.constants.annotations;
    exports com.rezzedup.util.constants.exceptions;
    exports com.rezzedup.util.constants.types;
}
