package net.chrisdolan.pcgen.drools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.drools.definition.KnowledgePackage;

public class CompileRules {
    public static void main(String[] args) throws ParseException, IOException {
        File dest = new File(args[0]);
        if (dest.exists()) {
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, "Overwrite file " + dest.getAbsolutePath() + "?"))
                return;
//            throw new IllegalStateException("dest file exist: " + dest.getAbsolutePath());
        }

        Engine engine = Engine.getEngine(new Ruleset("pathfinder"));
        Collection<KnowledgePackage> kpackages = engine.getKbase().getKnowledgePackages();

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dest));
        out.writeObject(kpackages);
        out.close();
    }
}
