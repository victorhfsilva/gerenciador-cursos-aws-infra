package com.myorg.stacks.ecs;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.constructs.Construct;

import java.util.Map;

public class ECSEurekaServerStack extends Stack {

    public ECSEurekaServerStack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public ECSEurekaServerStack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        CfnParameter eurekaServerUser = CfnParameter.Builder.create(this, "eurekaServerUser")
                .type("String")
                .description("Usuário do Eureka Server.")
                .build();

        CfnParameter eurekaServerPassword = CfnParameter.Builder.create(this, "eurekaServerPassword")
                .type("String")
                .description("Senha do Eureka Server")
                .build();

        ApplicationLoadBalancedFargateService fargateService = ApplicationLoadBalancedFargateService.Builder.create(this, "GerenciadorDeCursosServiceEurekaServer")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(1)
                .listenerPort(8761)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromRegistry("amazon/amazon-ecs-sample"))
                                .environment(Map.of(
                                        "EUREKA_SERVER_USER", eurekaServerUser.getValueAsString(),
                                        "EUREKA_SERVER_PASSWORD", eurekaServerPassword.getValueAsString()
                                ))
                                .containerPort(8761)
                                .containerName("GerenciadorDeCursosContainerUsuarios")
                                .build())
                .memoryLimitMiB(512)
                .publicLoadBalancer(false)
                .build();

        CfnOutput.Builder.create(this, "EurekaServerUser")
                .exportName("eureka-server-user")
                .value(eurekaServerUser.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "EurekaServerPassword")
                .exportName("eureka-server-password")
                .value(eurekaServerPassword.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "EurekaServerUrl")
                .exportName("eureka-server-url")
                .value(fargateService.getAssignPublicIp().toString())
                .build();
    }
}
