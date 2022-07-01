package me.ikevoodoo.smpcore.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CodeGenerator {

    private CodeGenerator() {

    }

    public static final int USE_ALPHABET = 2;
    public static final int USE_NUMBERS = 3;
    public static final int USE_SPECIAL = 4;

    private static final char[] alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] digits = "0123456789".toCharArray();
    private static final char[] special = "òàùè+ç°§é*@#[]{}!\"£$%&/()=?^'ì\\|/-+€<>,;.:_`~".toCharArray();

    private static final char[] alphabetDigits = new char[alphabet.length + digits.length];
    private static final char[] alphabetSpecial = new char[alphabet.length + special.length];
    private static final char[] specialDigits = new char[special.length + digits.length];
    private static final char[] alphabetSpecialDigits = new char[alphabetSpecial.length + digits.length];

    private static final SecureRandom random;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        System.arraycopy(alphabet, 0, alphabetDigits, 0, alphabet.length);
        System.arraycopy(digits, 0, alphabetDigits, alphabet.length, digits.length);

        System.arraycopy(alphabet, 0, alphabetSpecial, 0, alphabet.length);
        System.arraycopy(special, 0, alphabetSpecial, alphabet.length, special.length);

        System.arraycopy(special, 0, specialDigits, 0, special.length);
        System.arraycopy(digits, 0, specialDigits, special.length, digits.length);

        System.arraycopy(alphabet, 0, alphabetSpecialDigits, 0, alphabet.length);
        System.arraycopy(special, 0, alphabetSpecialDigits, alphabet.length, special.length);
        System.arraycopy(digits, 0, alphabetSpecialDigits, alphabet.length + special.length, digits.length);
    }

    public static String generate(int length, int flags) {
        char[] charset;
        char[] buffer = new char[length];

        if ((flags & USE_ALPHABET) == USE_ALPHABET && (flags & USE_SPECIAL) == USE_SPECIAL && (flags & USE_NUMBERS) == USE_NUMBERS)
            charset = alphabetSpecialDigits;

        else if ((flags & USE_ALPHABET) == USE_ALPHABET && (flags & USE_NUMBERS) == USE_NUMBERS)
            charset = alphabetDigits;

        else if ((flags & USE_ALPHABET) == USE_ALPHABET && (flags & USE_SPECIAL) == USE_SPECIAL)
            charset = alphabetSpecial;

        else if ((flags & USE_SPECIAL) == USE_SPECIAL && (flags & USE_NUMBERS) == USE_NUMBERS)
            charset = specialDigits;

        else if ((flags & USE_ALPHABET) == USE_ALPHABET)
            charset = alphabet;

        else if ((flags & USE_NUMBERS) == USE_NUMBERS)
            charset = digits;

        else if ((flags & USE_SPECIAL) == USE_SPECIAL)
            charset = special;
        else return "";

        for (int i = 0; i < length; i++)
            buffer[i] = charset[random.nextInt(charset.length)];

        return new String(buffer);
    }

}
