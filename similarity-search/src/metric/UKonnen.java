/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metric;

/**
 *
 * @author pccli
 */
public class UKonnen {
    
    
    public int ukkonen(String a, String b, int threshold) {
  if (a.equals(b)){
    return 0;
  }

  if (a.length() > b.length()) {
    // Swap a and b so b longer or same length as a
    String tmp = a;
    a = b;
    b = tmp;
  }

  int aLen = a.length();
  int bLen = b.length();

  // Performing suffix trimming:
  // We can linearly drop suffix common to both strings since they
  // don't increase distance at all
  // Note: `~-` is the bitwise way to perform a `- 1` operation
  while (aLen > 0 && a.charAt(aLen-1) == b.charAt(bLen-1)) {
    aLen--;
    bLen--;
  }

  if (aLen == 0) {
    return bLen < threshold ? bLen : threshold;
  }

  // Performing prefix trimming
  // We can linearly drop prefix common to both strings since they
  // don't increase distance at all
  int tStart = 0;
  while (tStart < aLen && a.charAt(tStart) == b.charAt(tStart)) {
    tStart++;
  }

  aLen -= tStart;
  bLen -= tStart;

  if (aLen == 0) {
    return bLen < threshold ? bLen : threshold;
  }

  threshold = bLen < threshold ? bLen : threshold;

  int dLen = bLen - aLen;

  if (threshold < dLen) {
    return threshold;
  }

  // floor(min(threshold, aLen) / 2)) + 2
  int ZERO_K = ((aLen < threshold ? aLen : threshold) >> 1) + 2;

  int arrayLength = dLen + ZERO_K * 2 + 2;
  int currentRow[] = new int[arrayLength];
  int nextRow[] = new int[arrayLength];
  for (int i = 0; i < arrayLength; i++) {
    currentRow[i] = -1;
    nextRow[i] = -1;
  }

  int aCharCodes[] = new int[aLen];
  int bCharCodes[] = new int[bLen];

  int i,t;
  for (i = 0, t = tStart; i < aLen; i++, t++) {
    aCharCodes[i] = a.charAt(t);
    bCharCodes[i] = b.charAt(t);
  }

  while (i < bLen) {
    bCharCodes[i++] = b.charAt(t++);
  }

  i = 0;
  int conditionRow = dLen + ZERO_K;
  int endMax = conditionRow << 1;
  do {
    i++;

    int tmp[] = currentRow;
    currentRow = nextRow;
    nextRow = tmp;

    int start;
    int previousCell;
    int currentCell = -1;
    int nextCell;

    if (i <= ZERO_K) {
      start = -i + 1;
      nextCell = i - 2;
    } else {
      start = i - (ZERO_K << 1) + 1;
      nextCell = currentRow[ZERO_K + start];
    }

    int end;
    if (i <= conditionRow) {
      end = i;
      nextRow[ZERO_K + i] = -1;
    } else {
      end = endMax - i;
    }

    for (int k = start, rowIndex = start + ZERO_K; k < end; k++, rowIndex++) {
      previousCell = currentCell;
      currentCell = nextCell;
      nextCell = currentRow[rowIndex + 1];

      // max(t, previousCell, nextCell + 1)
      t = currentCell + 1;
      t = t < previousCell ? previousCell : t;
      t = t < nextCell + 1 ? nextCell + 1 : t;

      while (t < aLen && t + k < bLen && aCharCodes[t] == bCharCodes[t + k]) {
        t++;
      }

      nextRow[rowIndex] = t;
    }
  } while (nextRow[conditionRow] < aLen && i <= threshold);

  return i - 1;
}


    public static void main(String[] args) {
        UKonnen u = new UKonnen();
        System.out.println(u.ukkonen("teste", "festa", 3));
    }
}
