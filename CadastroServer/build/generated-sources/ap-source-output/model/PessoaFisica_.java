package model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Pessoa;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2024-06-13T11:36:24", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(PessoaFisica.class)
public class PessoaFisica_ { 

    public static volatile SingularAttribute<PessoaFisica, Pessoa> pessoa;
    public static volatile SingularAttribute<PessoaFisica, String> cpf;
    public static volatile SingularAttribute<PessoaFisica, Integer> id;

}