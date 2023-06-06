package org.example.massege;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MassegeNewFile extends Massege{
    int toId;
    int fileId;
    byte[] fileName;
}
