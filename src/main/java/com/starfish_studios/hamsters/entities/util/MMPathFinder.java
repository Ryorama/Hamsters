package com.starfish_studios.hamsters.entities.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MMPathFinder extends PathFinder {

    // Credit to Mowzie's Mobs

    public MMPathFinder(NodeEvaluator processor, int maxVisitedNodes) {
        super(processor, maxVisitedNodes);
    }

    @Override
    public Path findPath(@NotNull PathNavigationRegion regionIn, @NotNull Mob mob, @NotNull Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
        Path path = super.findPath(regionIn, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);
        return path == null ? null : new PatchedPath(path);
    }

    static class PatchedPath extends Path {

        public PatchedPath(Path original) {
            super(copyPathPoints(original), original.getTarget(), original.canReach());
        }

        @Override
        public @NotNull Vec3 getEntityPosAtNode(Entity entity, int index) {
            Node point = this.getNode(index);
            double x = point.x + Mth.floor(entity.getBbWidth() + 1.0F) * 0.5D;
            double y = point.y;
            double z = point.z + Mth.floor(entity.getBbWidth() + 1.0F) * 0.5D;
            return new Vec3(x, y, z);
        }

        private static List<Node> copyPathPoints(Path original) {
            List<Node> points = new ArrayList<>();
            for (int nodeCount = 0; nodeCount < original.getNodeCount(); nodeCount++) points.add(original.getNode(nodeCount));
            return points;
        }
    }
}