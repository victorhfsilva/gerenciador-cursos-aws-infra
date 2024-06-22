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

public class ECSApiGatewayStack extends Stack {
    public ECSApiGatewayStack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public ECSApiGatewayStack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        ApplicationLoadBalancedFargateService.Builder.create(this, "GerenciadorDeCursosServiceCursos")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(1)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromRegistry("victorhfsilva/gerenciamento-cursos-api-gateway"))
                                .environment(Map.of(
                                        "EUREKA_SERVER_USER", Fn.importValue("eureka-server-user"),
                                        "EUREKA_SERVER_PASSWORD", Fn.importValue("eureka-server-password"),
                                        "EUREKA_SERVER_URL", Fn.importValue("eureka-server-url")
                                ))
                                .containerPort(8080)
                                .containerName("GerenciadorDeCursosContainerCursos")
                                .build())
                .memoryLimitMiB(512)
                .publicLoadBalancer(true)
                .build();
    }
}
