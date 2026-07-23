/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package com.zigythebird.playeranimcore.util;

import com.zigythebird.playeranimcore.bones.PivotBone;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.math.Vec3f;
import java.util.function.Function;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MatrixUtil {
    public static void translateMatrixForBone(Matrix4f matrix, PlayerAnimBone bone) {
        matrix.translate(-bone.position.x, bone.position.y, -bone.position.z);
    }

    public static void rotateMatrixAroundBone(Matrix4f matrix, PlayerAnimBone bone) {
        if (bone.rotation.z != 0.0f || bone.rotation.y != 0.0f || bone.rotation.x != 0.0f) {
            matrix.rotateZYX(bone.rotation);
        }
    }

    public static void scaleMatrixForBone(Matrix4f matrix, PlayerAnimBone bone) {
        matrix.scale(bone.scale.x, bone.scale.y, bone.scale.z);
    }

    public static void translateToPivotPoint(Matrix4f matrix, Vec3f pivot) {
        matrix.translate(pivot.x(), pivot.y(), -pivot.z());
    }

    public static void translateAwayFromPivotPoint(Matrix4f matrix, Vec3f pivot) {
        matrix.translate(-pivot.x(), -pivot.y(), pivot.z());
    }

    public static void prepMatrixForBone(Matrix4f matrix, PlayerAnimBone bone, Vec3f pivot) {
        MatrixUtil.translateToPivotPoint(matrix, pivot);
        MatrixUtil.translateMatrixForBone(matrix, bone);
        MatrixUtil.rotateMatrixAroundBone(matrix, bone);
        MatrixUtil.scaleMatrixForBone(matrix, bone);
        MatrixUtil.translateAwayFromPivotPoint(matrix, pivot);
    }

    public static void applyParentsToChild(PlayerAnimBone child, Iterable<? extends PlayerAnimBone> parents, Function<String, Vec3f> positions) {
        Matrix4f matrix = new Matrix4f();
        for (PlayerAnimBone playerAnimBone : parents) {
            Vec3f vec3f;
            if (playerAnimBone instanceof PivotBone) {
                PivotBone pivotBone = (PivotBone)playerAnimBone;
                vec3f = pivotBone.getPivot();
            } else {
                vec3f = positions.apply(playerAnimBone.getName());
            }
            Vec3f pivot = vec3f;
            MatrixUtil.prepMatrixForBone(matrix, playerAnimBone, pivot);
        }
        MatrixUtil.applyMatrixToBone(child, matrix, positions.apply(child.getName()));
    }

    public static void applyMatrixToBone(PlayerAnimBone bone, Matrix4f matrix, Vec3f pivot) {
        MatrixUtil.translateToPivotPoint(matrix, pivot);
        MatrixUtil.translateMatrixForBone(matrix, bone);
        MatrixUtil.rotateMatrixAroundBone(matrix, bone);
        bone.position.set(-matrix.m30() + pivot.x(), matrix.m31() - pivot.y(), -matrix.m32() + pivot.z());
        Vector3f rotation = matrix.getEulerAnglesZYX(new Vector3f());
        bone.rotation.set(rotation.x(), rotation.y(), rotation.z());
        Vector3f scale = matrix.getScale(new Vector3f());
        bone.scale.mul(scale.x(), scale.y(), scale.z());
    }
}

