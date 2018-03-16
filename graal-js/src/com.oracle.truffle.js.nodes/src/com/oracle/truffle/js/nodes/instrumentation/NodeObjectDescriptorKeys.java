/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.oracle.truffle.js.nodes.instrumentation;

import java.util.Map;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class NodeObjectDescriptorKeys implements TruffleObject {

    private final Object[] keys;

    NodeObjectDescriptorKeys(Map<String, Object> from) {
        this.keys = from.keySet().toArray();
    }

    @Override
    public ForeignAccess getForeignAccess() {
        return NodeObjectDescriptorKeysFactoryForeign.ACCESS;
    }

    public Object getKeyAt(int pos) {
        return keys[pos];
    }

    public int size() {
        return keys.length;
    }

    static boolean isInstance(TruffleObject object) {
        return object instanceof NodeObjectDescriptorKeys;
    }
}
