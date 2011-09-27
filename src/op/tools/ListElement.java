/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package op.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author tloehr
 */
public class ListElement implements Comparable<ListElement> {

    private String value;
    private long pk;
    private String data;
    private String prefix;
    private Object o;

    public ListElement(String value, long pk) {
        this.value = value;

        this.pk = pk;
        this.data = "";
        this.prefix = "";
        this.o = null;
    }

    public ListElement(String value, long pk, String prefix) {
        this(value, pk);
        // The 0 symbol shows a digit or 0 if no digit present
        NumberFormat formatter = new DecimalFormat("000");
        String s = formatter.format(pk);
        this.prefix = prefix + s + "-";
        this.o = null;
    }

    public ListElement(String value, long pk, Object o) {
        this(value, pk);
        this.o = o;
    }

    public ListElement(String value, String data) {
        this.value = value;
        this.pk = 0;
        this.prefix = "";
        this.o = null;
        this.data = data;
    }

    public ListElement(String value, String data, long pk) {
        this.value = value;
        this.pk = pk;
        this.prefix = "";
        this.o = null;
        this.data = data;
    }

    public ListElement(String value, Object o) {
        this.value = value;
        this.pk = 0;
        this.prefix = "";
        this.o = o;
        this.data = "";
    }

    public int compareTo(ListElement that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        int result = EQUAL;

        if (this == that) {
            result = EQUAL;
        } else {
            result = this.toString().compareTo(that.toString());
//            if (this.pk != 0) { // entweder über den PK
//                result = EQUAL;
//                if (this.pk < that.pk) {
//                    result = BEFORE;
//                }
//                if (this.pk > that.pk) {
//                    result = AFTER;
//                }
//            } else { // oder über data
//                result = this.data.compareTo(that.data);
//            }
        }
        return result;
    }

    public String toString() {
        return this.prefix + this.value;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long a) {
        pk = a;
    }

    public void setValue(String a) {
        value = a;
    }

    public String getData() {
        return data;
    }

    public String getValue() {
        return value;
    }

    public Object getObject() {
        return o;
    }
} // ListElement


