package com.myorg.stacks.ecs;

import software.amazon.awscdk.Fn;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.constructs.Construct;

import java.util.Map;

public class ECSUsuariosStack extends Stack {
    public ECSUsuariosStack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public ECSUsuariosStack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        ApplicationLoadBalancedFargateService.Builder.create(this, "GerenciadorDeCursosServiceUsuarios")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(1)
                .listenerPort(8081)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromRegistry("victorhfsilva/gerenciamento-cursos-usuarios-ms"))
                                .environment(Map.of(
                                        "POSTGRES_USER", Fn.importValue("db-usuarios-user"),
                                        "POSTGRES_PWD", Fn.importValue("db-usuarios-password"),
                                        "POSTGRES_DB", Fn.importValue("db-usuarios-name"),
                                        "DEV_DB_URL", "jdbc:postgresql://" + Fn.importValue("db-usuarios-endpoint") + ":" + Fn.importValue("db-usuarios-port")+"/" + Fn.importValue("db-usuarios-name"),
                                        "EUREKA_SERVER_USER", Fn.importValue("eureka-server-user"),
                                        "EUREKA_SERVER_PASSWORD", Fn.importValue("eureka-server-password"),
                                        "EUREKA_SERVER_URL", Fn.importValue("eureka-server-url")
                                ))
                                .containerPort(8081)
                                .containerName("GerenciadorDeCursosContainerUsuarios")
                                .build())
                .memoryLimitMiB(512)
                .publicLoadBalancer(false)
                .build();
    }
}
