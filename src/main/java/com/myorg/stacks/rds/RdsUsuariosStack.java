package com.myorg.stacks.rds;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Arrays;

public class RdsUsuariosStack extends Stack {

    public RdsUsuariosStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public RdsUsuariosStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        CfnParameter dbUsuariosUser = CfnParameter.Builder.create(this, "dbUsuariosUser")
                .type("String")
                .description("Usuário do banco de dados de usuários")
                .build();

        CfnParameter dbUsuariosPassword = CfnParameter.Builder.create(this, "dbUsuariosPassword")
                .type("String")
                .description("Senha do banco de dados de usuários")
                .build();

        CfnParameter dbUsuariosName = CfnParameter.Builder.create(this, "dbUsuariosName")
                .type("String")
                .description("Nome do banco de dados de usuários")
                .build();

        ISecurityGroup securityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(5432));

        DatabaseInstance databaseInstanceUsuarios = DatabaseInstance.Builder
                .create(this, "DBUsuarios")
                .instanceIdentifier("db-usuarios")
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
                .databaseName(dbUsuariosName.getValueAsString())
                .credentials(Credentials.fromUsername(dbUsuariosUser.getValueAsString(),
                        CredentialsFromUsernameOptions.builder()
                                .password(SecretValue.unsafePlainText(dbUsuariosPassword.getValueAsString())).build()))
                .build();

        CfnOutput.Builder.create(this, "dbUsuariosEndpoint")
                .exportName("db-usuarios-endpoint")
                .value(databaseInstanceUsuarios.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "dbUsuariosPort")
                .exportName("db-usuarios-port")
                .value(databaseInstanceUsuarios.getDbInstanceEndpointPort())
                .build();

        CfnOutput.Builder.create(this, "dbUsuariosName")
                .exportName("db-usuarios-name")
                .value(dbUsuariosName.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "dbUsuariosUser")
                .exportName("db-usuarios-user")
                .value(dbUsuariosUser.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "dbUsuariosPassword")
                .exportName("db-usuarios-password")
                .value(dbUsuariosPassword.getValueAsString())
                .build();
    }
}
