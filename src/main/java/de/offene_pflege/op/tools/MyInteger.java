package de.offene_pflege.op.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyInteger {
    int myInt = 0;

    public int increment() {
        myInt++;
        return myInt;
    }

    public int decrement() {
        myInt--;
        return myInt;
    }
}
