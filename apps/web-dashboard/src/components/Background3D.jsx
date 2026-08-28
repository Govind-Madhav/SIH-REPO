import React, { useRef, useMemo, useEffect } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { Points, PointMaterial } from '@react-three/drei';
import * as THREE from 'three';

// Generate random particles in a sphere
const generateParticles = (count) => {
    const positions = new Float32Array(count * 3);
    for (let i = 0; i < count; i++) {
        const r = 10 * Math.cbrt(Math.random());
        const theta = Math.random() * 2 * Math.PI;
        const phi = Math.acos(2 * Math.random() - 1);

        positions[i * 3] = r * Math.sin(phi) * Math.cos(theta);
        positions[i * 3 + 1] = r * Math.sin(phi) * Math.sin(theta);
        positions[i * 3 + 2] = r * Math.cos(phi);
    }
    return positions;
};

const ParticleCloud = ({ mouse }) => {
    const ref = useRef();
    const particleCount = 4000;

    const positions = useMemo(() => generateParticles(particleCount), [particleCount]);

    useFrame((state, delta) => {
        // Rotation over time
        ref.current.rotation.y += delta * 0.05;
        ref.current.rotation.x += delta * 0.02;

        // Parallax effect based on mouse position
        const targetX = (mouse.current.x * Math.PI) * 0.2;
        const targetY = (mouse.current.y * Math.PI) * 0.2;

        ref.current.rotation.y += 0.05 * (targetX - ref.current.rotation.y);
        ref.current.rotation.x += 0.05 * (targetY - ref.current.rotation.x);
    });

    return (
        <group rotation={[0, 0, Math.PI / 4]}>
            <Points ref={ref} positions={positions} stride={3} frustumCulled={false}>
                <PointMaterial
                    transparent
                    color="#8b5cf6"
                    size={0.08}
                    sizeAttenuation={true}
                    depthWrite={false}
                    blending={THREE.AdditiveBlending}
                />
            </Points>
        </group>
    );
};

export default function Background3D() {
    const mouse = useRef({ x: 0, y: 0 });

    useEffect(() => {
        const handleMouseMove = (event) => {
            // Normalize mouse coordinates from -1 to 1
            mouse.current.x = (event.clientX / window.innerWidth) * 2 - 1;
            mouse.current.y = -(event.clientY / window.innerHeight) * 2 + 1;
        };

        window.addEventListener('mousemove', handleMouseMove);
        return () => window.removeEventListener('mousemove', handleMouseMove);
    }, []);

    return (
        <div className="fixed inset-0 z-0 bg-slate-950 pointer-events-none">
            {/* Background Gradient to make it extra aesthetic */}
            <div className="absolute inset-0 bg-gradient-to-br from-indigo-900/30 to-purple-900/10 pointer-events-none" />

            <Canvas camera={{ position: [0, 0, 15], fov: 60 }}>
                <ambientLight intensity={0.5} />
                <ParticleCloud mouse={mouse} />
            </Canvas>
        </div>
    );
}
