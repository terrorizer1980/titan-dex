/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.titan.dexlib.dx.rop.code;

import com.baidu.titan.dexlib.dx.rop.cst.CstString;
import com.baidu.titan.dexlib.dx.util.Hex;

import java.util.HashSet;

/**
 * Information about a source position for code, which includes both a
 * line number and original bytecode address.
 */
public final class SourcePosition {
    /** {@code non-null;} convenient "no information known" instance */
    public static final SourcePosition NO_INFO =
        new SourcePosition(null, -1, -1);

    /** {@code null-ok;} name of the file of origin or {@code null} if unknown */
    private final CstString sourceFile;

    /**
     * {@code >= -1;} the bytecode address, or {@code -1} if that
     * information is unknown
     */
    private final int address;

    /**
     * {@code >= -1;} the line number, or {@code -1} if that
     * information is unknown
     */
    private int line;

    private int[] lines;

    /**
     * Constructs an instance.
     *
     * @param sourceFile {@code null-ok;} name of the file of origin or
     * {@code null} if unknown
     * @param address {@code >= -1;} original bytecode address or {@code -1}
     * if unknown
     * @param line {@code >= -1;} original line number or {@code -1} if
     * unknown
     */
    public SourcePosition(CstString sourceFile, int address, int line) {
        if (address < -1) {
            throw new IllegalArgumentException("address < -1");
        }

        if (line < -1) {
            throw new IllegalArgumentException("line < -1");
        }

        this.sourceFile = sourceFile;
        this.address = address;
        this.line = line;
    }


    public SourcePosition(CstString sourceFile, int address, int[] lines) {
        if (address < -1) {
            throw new IllegalArgumentException("address < -1");
        }

        if (lines == null || lines.length < 1) {
            throw new IllegalArgumentException("line < -1");
        }

        this.sourceFile = sourceFile;
        this.address = address;
        this.lines = lines;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(50);

        if (sourceFile != null) {
            sb.append(sourceFile.toHuman());
            sb.append(":");
        }

        if (line >= 0) {
            sb.append(line);
        }

        sb.append('@');

        if (address < 0) {
            sb.append("????");
        } else {
            sb.append(Hex.u2(address));
        }

        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SourcePosition)) {
            return false;
        }

        if (this == other) {
            return true;
        }

        SourcePosition pos = (SourcePosition) other;

        return (address == pos.address) && sameLineAndFile(pos);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return sourceFile.hashCode() + address + line;
    }

    /**
     * Returns whether the lines match between this instance and
     * the one given.
     *
     * @param other {@code non-null;} the instance to compare to
     * @return {@code true} iff the lines match
     */
    public boolean sameLine(SourcePosition other) {
        if (lines == other.lines) {
            return true;
        }
        int lineSz = lines == null ? 0 : lines.length;
        int otherLineSz = other.lines == null ? 0 : other.lines.length;
        if (lineSz != otherLineSz) {
            return false;
        }

        HashSet<Integer> lineSet = new HashSet<>();
        if (lineSz > 0) {
            for (Integer line: lines) {
                lineSet.add(line);
            }
        }
        HashSet<Integer> otherLineSet = new HashSet<>();
        if (otherLineSz > 0) {
            for (Integer line: other.lines) {
                otherLineSet.add(line);
            }
        }

        return lineSet.equals(otherLineSet);
                //(line == other.line);
    }

    /**
     * Returns whether the lines and files match between this instance and
     * the one given.
     *
     * @param other {@code non-null;} the instance to compare to
     * @return {@code true} iff the lines and files match
     */
    public boolean sameLineAndFile(SourcePosition other) {
        return (line == other.line) &&
            ((sourceFile == other.sourceFile) ||
             ((sourceFile != null) && sourceFile.equals(other.sourceFile)));
    }

    /**
     * Gets the source file, if known.
     *
     * @return {@code null-ok;} the source file or {@code null} if unknown
     */
    public CstString getSourceFile() {
        return sourceFile;
    }

    /**
     * Gets the original bytecode address.
     *
     * @return {@code >= -1;} the address or {@code -1} if unknown
     */
    public int getAddress() {
        return address;
    }

    /**
     * Gets the original line number.
     *
     * @return {@code >= -1;} the original line number or {@code -1} if
     * unknown
     */
    public int getLine() {
        if (lines != null && lines.length > 0) {
            return lines[0];
        }
        return line;
    }

    public int[] getLines() {
        return lines;
    }

}
