package com.myorg.stacks.rds;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Arrays;

public class RdsCursosStack extends Stack {
    public RdsCursosStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public RdsCursosStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        CfnParameter dbCursosUser = CfnParameter.Builder.create(this, "dbCursosUser")
                .type("String")
                .description("Usuário do banco de dados de cursos")
                .build();

        CfnParameter dbCursosPassword = CfnParameter.Builder.create(this, "dbCursosPassword")
                .type("String")
                .description("Senha do banco de dados de cursos")
                .build();

        CfnParameter dbCursosName = CfnParameter.Builder.create(this, "dbCursosName")
                .type("String")
                .description("Nome do banco de dados de cursos")
                .build();

        ISecurityGroup securityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(5432));

        DatabaseInstance databaseInstanceCursos = DatabaseInstance.Builder
                .create(this, "DBCursos")
                .instanceIdentifier("db-cursos")
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_16_3)
                        .build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .multiAz(false)
                .allocatedStorage(10)
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPrivateSubnets())
                        .build())
                .securityGroups(Arrays.asList(securityGroup))
                .databaseName(dbCursosName.getValueAsString())
                .credentials(Credentials.fromUsername(dbCursosUser.getValueAsString(),
                        CredentialsFromUsernameOptions.builder()
                                .password(SecretValue.unsafePlainText(dbCursosPassword.getValueAsString())).build()))
                .build();

        CfnOutput.Builder.create(this, "DBCursosEndpoint")
                .exportName("db-cursos-endpoint")
                .value(databaseInstanceCursos.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "DBCursosPort")
                .exportName("db-cursos-port")
                .value(databaseInstanceCursos.getDbInstanceEndpointPort())
                .build();

        CfnOutput.Builder.create(this, "DBCursosName")
                .exportName("db-cursos-name")
                .value(dbCursosName.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "DBCursosUser")
                .exportName("db-cursos-user")
                .value(dbCursosUser.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "DBCursosPassword")
                .exportName("db-cursos-password")
                .value(dbCursosPassword.getValueAsString())
                .build();
    }
}
