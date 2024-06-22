package com.myorg.stacks.ecs;

import software.amazon.awscdk.Fn;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.*;

import java.util.Map;


public class ECSCursosStack extends Stack {
    public ECSCursosStack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public ECSCursosStack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        ApplicationLoadBalancedFargateService.Builder.create(this, "GerenciadorDeCursosServiceCursos")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(1)
                .listenerPort(8082)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromRegistry("victorhfsilva/gerenciamento-cursos-cursos-ms\n"))
                                .environment(Map.of(
                                        "POSTGRES_USER", Fn.importValue("db-cursos-user"),
                                        "POSTGRES_PWD", Fn.importValue("db-cursos-password"),
                                        "POSTGRES_DB", Fn.importValue("db-cursos-name"),
                                        "DEV_DB_URL", "jdbc:postgresql://" + Fn.importValue("db-cursos-endpoint") + ":" + Fn.importValue("db-cursos-port")+"/" + Fn.importValue("db-cursos-name"),
                                        "EUREKA_SERVER_USER", Fn.importValue("eureka-server-user"),
                                        "EUREKA_SERVER_PASSWORD", Fn.importValue("eureka-server-password"),
                                        "EUREKA_SERVER_URL", Fn.importValue("eureka-server-url")
                                ))
                                .containerPort(8082)
                                .containerName("GerenciadorDeCursosContainerCursos")
                                .build())
                .memoryLimitMiB(512)
                .publicLoadBalancer(false)
                .build();
    }
}
