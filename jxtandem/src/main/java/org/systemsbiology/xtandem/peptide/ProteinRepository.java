package org.systemsbiology.xtandem.peptide;

import org.systemsbiology.xtandem.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.peptide.ProteinRepository
 * Simple code to maintain a
 * User: steven
 * Date: 9/16/11
 */
public class ProteinRepository {
    public static final ProteinRepository[] EMPTY_ARRAY = {};

    private final IMainData m_Tandem;
      private final Map<Integer, String> m_IdToAnnotation = new HashMap<Integer, String>();
    private final Map<String, Integer> m_AnnotationToId = new HashMap<String, Integer>();

    public ProteinRepository(final IMainData pTandem ) {
        m_Tandem = pTandem;
      }


    public int getNumberProteins()
    {
        return m_AnnotationToId.size();
    }
    /**
     * read a fasta file to populate the repository
     * @param is
     */
    public void populate(InputStream is) {
        LineNumberReader rdr = null;
        try {
            rdr = new LineNumberReader(new InputStreamReader(is));
            String line = rdr.readLine();
            while (line != null) {
                if (line.startsWith(">")) {
                    String annotation = line.substring(1).trim();
                    Integer id = getId(annotation);
                    if (id == null) {
                        id = getNumberProteins() + 1;  // 0 is not valid
                        m_AnnotationToId.put(annotation, id);
                        m_IdToAnnotation.put(id, annotation);
                    }
                }
                line = rdr.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if (rdr != null) {
                try {
                    rdr.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        }
    }

    /**
     * look up a protein by id
     * @param key  !null id
     * @return possibily null annotation
     */
    public String getAnnotation(Integer key) {
      return m_IdToAnnotation.get(key);
  }

    public Integer getId(String annotation) {
      return m_AnnotationToId.get(annotation);
  }

    /**
     * quick main to count proteins in a fasta file
     * @param args filename
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
         ProteinRepository pr = new ProteinRepository(null);
        String fileName = args[0];
        InputStream is = new FileInputStream(fileName);
         pr.populate(is);
        System.out.println(fileName  + " has " + pr.getNumberProteins());
    }
}
