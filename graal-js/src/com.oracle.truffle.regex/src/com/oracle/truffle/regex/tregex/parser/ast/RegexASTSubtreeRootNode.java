/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.regex.tregex.parser.ast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.regex.tregex.parser.ast.visitors.RegexASTVisitorIterable;
import com.oracle.truffle.regex.tregex.util.DebugUtil;

/**
 * A common supertype to the root node and look-ahead and look-behind assertions. Every AST subtree
 * contains a {@link Group} which contains the syntactic subtree, as well as a {@link MatchFound}
 * node, which is needed for NFA-like traversal of the AST, see
 * {@link com.oracle.truffle.regex.tregex.parser.ast.visitors.NFATraversalRegexASTVisitor}.
 */
public abstract class RegexASTSubtreeRootNode extends Term implements RegexASTVisitorIterable {

    private Group group;
    private MatchFound matchFound;
    private boolean visitorGroupVisited = false;

    RegexASTSubtreeRootNode() {
    }

    RegexASTSubtreeRootNode(RegexASTSubtreeRootNode copy, RegexAST ast) {
        super(copy);
        setGroup(copy.group.copy(ast));
        ast.createEndPoint(this);
    }

    @Override
    public abstract RegexASTSubtreeRootNode copy(RegexAST ast);

    /**
     * Returns the {@link Group} that represents the contents of this subtree.
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Sets the contents of this subtree.
     * <p>
     * This method should be called after creating any instance of this class. Otherwise, methods of
     * this class could throw {@link NullPointerException}s or return {@code null}s.
     */
    public void setGroup(Group group) {
        this.group = group;
        group.setParent(this);
    }

    /**
     * Returns this subtree's corresponding {@link MatchFound} node.
     */
    public MatchFound getMatchFound() {
        return matchFound;
    }

    public void setMatchFound(MatchFound matchFound) {
        this.matchFound = matchFound;
        matchFound.setParent(this);
    }

    /**
     * Marks the node as dead, i.e. unmatchable.
     * <p>
     * Note that using this setter also traverses the ancestors and children of this node and
     * updates their "dead" status as well.
     */
    @Override
    public void markAsDead() {
        super.markAsDead();
        if (!group.isDead()) {
            group.markAsDead();
        }
    }

    @Override
    public boolean visitorHasNext() {
        return !visitorGroupVisited;
    }

    @Override
    public RegexASTNode visitorGetNext(boolean reverse) {
        visitorGroupVisited = true;
        return group;
    }

    @Override
    public void resetVisitorIterator() {
        visitorGroupVisited = false;
    }

    public abstract String getPrefix();

    @Override
    @CompilerDirectives.TruffleBoundary
    public String toString() {
        return "(" + getPrefix() + group.alternativesToString() + ")";
    }

    @Override
    public DebugUtil.Table toTable(String name) {
        return super.toTable(name).append(new DebugUtil.Value("group", astNodeId(group)));
    }
}
