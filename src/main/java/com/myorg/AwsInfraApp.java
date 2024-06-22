package com.myorg;

import com.myorg.stacks.rds.RdsCursosStack;
import com.myorg.stacks.rds.RdsUsuariosStack;
import com.myorg.stacks.ClusterStack;
import com.myorg.stacks.VpcStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;

public class AwsInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpcStack = new VpcStack(app, "GerenciadorDeCursosVpc");
        Vpc vpc = vpcStack.getVpc();

        ClusterStack clusterStack = new ClusterStack(app, "GerenciadorDeCursosCluster", vpc);
        Cluster cluster = clusterStack.getCluster();
        clusterStack.addDependency(vpcStack);

        RdsCursosStack rdsCursosStack = new RdsCursosStack(app, "GerenciadorDeCursosRdsCursos", vpc);
        rdsCursosStack.addDependency(vpcStack);

        RdsUsuariosStack rdsUsuariosStack = new RdsUsuariosStack(app, "GerenciadorDeCursosRdsUsuarios", vpc);
        rdsUsuariosStack.addDependency(vpcStack);

        app.synth();
    }
}

