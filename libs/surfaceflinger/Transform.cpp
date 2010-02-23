/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <math.h>

#include <cutils/compiler.h>
#include <utils/String8.h>
#include <ui/Region.h>

#include "Transform.h"

// ---------------------------------------------------------------------------

namespace android {

// ---------------------------------------------------------------------------

template <typename T> inline T min(T a, T b) {
    return a<b ? a : b;
}
template <typename T> inline T min(T a, T b, T c) {
    return min(a, min(b, c));
}
template <typename T> inline T min(T a, T b, T c, T d) {
    return min(a, b, min(c, d));
}

template <typename T> inline T max(T a, T b) {
    return a>b ? a : b;
}
template <typename T> inline T max(T a, T b, T c) {
    return max(a, max(b, c));
}
template <typename T> inline T max(T a, T b, T c, T d) {
    return max(a, b, max(c, d));
}

// ---------------------------------------------------------------------------

Transform::Transform() {
    reset();
}

Transform::Transform(const Transform&  other)
    : mMatrix(other.mMatrix), mType(other.mType) {
}

Transform::Transform(uint32_t orientation) {
    set(orientation, 0, 0);
}

Transform::~Transform() {
}


bool Transform::absIsOne(float f) {
    return fabs(f) == 1.0f;
}

bool Transform::isZero(float f) {
    return fabs(f) == 0.0f;
}

bool Transform::absEqual(float a, float b) {
    return fabs(a) == fabs(b);
}

Transform Transform::operator * (const Transform& rhs) const
{
    if (CC_LIKELY(mType == IDENTITY))
        return rhs;

    Transform r(*this);
    if (rhs.mType == IDENTITY)
        return r;

    // TODO: we could use mType to optimize the matrix multiply
    const mat33& A(mMatrix);
    const mat33& B(rhs.mMatrix);
          mat33& D(r.mMatrix);
    for (int i=0 ; i<3 ; i++) {
        const float v0 = A[0][i];
        const float v1 = A[1][i];
        const float v2 = A[2][i];
        D[0][i] = v0*B[0][0] + v1*B[0][1] + v2*B[0][2];
        D[1][i] = v0*B[1][0] + v1*B[1][1] + v2*B[1][2];
        D[2][i] = v0*B[2][0] + v1*B[2][1] + v2*B[2][2];
    }
    r.mType |= rhs.mType;

    // TODO: we could recompute this value from r and rhs
    r.mType &= 0xFF;
    r.mType |= UNKNOWN_TYPE;
    return r;
}

float const* Transform::operator [] (int i) const {
    return mMatrix[i].v;
}

bool Transform::transformed() const {
    return type() > TRANSLATE;
}

int Transform::tx() const {
    return floorf(mMatrix[2][0] + 0.5f);
}

int Transform::ty() const {
    return floorf(mMatrix[2][1] + 0.5f);
}

void Transform::reset() {
    mType = IDENTITY;
    for(int i=0 ; i<3 ; i++) {
        vec3& v(mMatrix[i]);
        for (int j=0 ; j<3 ; j++)
            v[j] = ((i==j) ? 1.0f : 0.0f);
    }
}

void Transform::set(float tx, float ty)
{
    mMatrix[2][0] = tx;
    mMatrix[2][1] = ty;
    mMatrix[2][2] = 1.0f;

    if (isZero(tx) && isZero(ty)) {
        mType &= ~TRANSLATE;
    } else {
        mType |= TRANSLATE;
    }
}

void Transform::set(float a, float b, float c, float d)
{
    mat33& M(mMatrix);
    M[0][0] = a;    M[1][0] = b;
    M[0][1] = c;    M[1][1] = d;
    M[0][2] = 0;    M[1][2] = 0;
    mType = UNKNOWN_TYPE;
}

void Transform::set(uint32_t flags, float w, float h)
{
    mType = flags << 8;
    float sx = (flags & FLIP_H) ? -1 : 1;
    float sy = (flags & FLIP_V) ? -1 : 1;
    float a=0, b=0, c=0, d=0, x=0, y=0;
    int xmask = 0;

    // computation of x,y
    // x y
    // 0 0  0
    // w 0  ROT90
    // w h  FLIPH|FLIPV
    // 0 h  FLIPH|FLIPV|ROT90

    if (flags & ROT_90) {
        mType |= ROTATE;
        b = -sy;
        c = sx;
        xmask = 1;
    } else {
        a = sx;
        d = sy;
    }

    if (flags & FLIP_H) {
        mType ^= SCALE;
        xmask ^= 1;
    }

    if (flags & FLIP_V) {
        mType ^= SCALE;
        y = h;
    }

    if ((flags & ROT_180) == ROT_180) {
        mType |= ROTATE;
    }

    if (xmask) {
        x = w;
    }

    if (!isZero(x) || !isZero(y)) {
        mType |= TRANSLATE;
    }

    mat33& M(mMatrix);
    M[0][0] = a;    M[1][0] = b;    M[2][0] = x;
    M[0][1] = c;    M[1][1] = d;    M[2][1] = y;
    M[0][2] = 0;    M[1][2] = 0;    M[2][2] = 1;
}

Transform::vec2 Transform::transform(const vec2& v) const {
    vec2 r;
    const mat33& M(mMatrix);
    r[0] = M[0][0]*v[0] + M[1][0]*v[1] + M[2][0];
    r[1] = M[0][1]*v[0] + M[1][1]*v[1] + M[2][1];
    return r;
}

Transform::vec3 Transform::transform(const vec3& v) const {
    vec3 r;
    const mat33& M(mMatrix);
    r[0] = M[0][0]*v[0] + M[1][0]*v[1] + M[2][0]*v[2];
    r[1] = M[0][1]*v[0] + M[1][1]*v[1] + M[2][1]*v[2];
    r[2] = M[0][2]*v[0] + M[1][2]*v[1] + M[2][2]*v[2];
    return r;
}

void Transform::transform(fixed1616* point, int x, int y) const
{
    const float toFixed = 65536.0f;
    const mat33& M(mMatrix);
    vec2 v(x, y);
    v = transform(v);
    point[0] = v[0] * toFixed;
    point[1] = v[1] * toFixed;
}

Rect Transform::makeBounds(int w, int h) const
{
    return transform( Rect(w, h) );
}

Rect Transform::transform(const Rect& bounds) const
{
    Rect r;
    vec2 lt( bounds.left,  bounds.top    );
    vec2 rt( bounds.right, bounds.top    );
    vec2 lb( bounds.left,  bounds.bottom );
    vec2 rb( bounds.right, bounds.bottom );

    lt = transform(lt);
    rt = transform(rt);
    lb = transform(lb);
    rb = transform(rb);

    r.left   = floorf(min(lt[0], rt[0], lb[0], rb[0]) + 0.5f);
    r.top    = floorf(min(lt[1], rt[1], lb[1], rb[1]) + 0.5f);
    r.right  = floorf(max(lt[0], rt[0], lb[0], rb[0]) + 0.5f);
    r.bottom = floorf(max(lt[1], rt[1], lb[1], rb[1]) + 0.5f);

    return r;
}

Region Transform::transform(const Region& reg) const
{
    Region out;
    if (CC_UNLIKELY(transformed())) {
        if (CC_LIKELY(preserveRects())) {
            Region::const_iterator it = reg.begin();
            Region::const_iterator const end = reg.end();
            while (it != end) {
                out.orSelf(transform(*it++));
            }
        } else {
            out.set(transform(reg.bounds()));
        }
    } else {
        out = reg.translate(tx(), ty());
    }
    return out;
}

uint32_t Transform::type() const
{
    if (mType & UNKNOWN_TYPE) {
        // recompute what this transform is

        const mat33& M(mMatrix);
        const float a = M[0][0];
        const float b = M[1][0];
        const float c = M[0][1];
        const float d = M[1][1];
        const float x = M[2][0];
        const float y = M[2][1];

        bool scale = false;
        uint32_t flags = ROT_0;
        if (isZero(b) && isZero(c)) {
            if (absEqual(a, d)) {
                if (a<0)    flags |= FLIP_H;
                if (d<0)    flags |= FLIP_V;
                if (!absIsOne(a) || !absIsOne(d)) {
                    scale = true;
                }
            } else {
                flags = ROT_INVALID;
            }
        } else if (isZero(a) && isZero(d)) {
            if (absEqual(b, c)) {
                flags |= ROT_90;
                if (b>0)    flags |= FLIP_H;
                if (c<0)    flags |= FLIP_V;
                if (!absIsOne(b) || !absIsOne(c)) {
                    scale = true;
                }
            } else {
                flags = ROT_INVALID;
            }
        } else {
            flags = ROT_INVALID;
        }

        mType = flags << 8;
        if (flags & ROT_INVALID) {
            mType |= UNKNOWN;
        } else {
            if ((flags & ROT_90) || ((flags & ROT_180) == ROT_180))
                mType |= ROTATE;
            if (flags & FLIP_H)
                mType ^= SCALE;
            if (flags & FLIP_V)
                mType ^= SCALE;
            if (scale)
                mType |= SCALE;
        }

        if (!isZero(x) || !isZero(y))
            mType |= TRANSLATE;
    }
    return mType;
}

uint32_t Transform::getType() const {
    return type() & 0xFF;
}

uint32_t Transform::getOrientation() const
{
    return (type() >> 8) & 0xFF;
}

bool Transform::preserveRects() const
{
    return (type() & ROT_INVALID) ? false : true;
}

void Transform::dump(const char* name) const
{
    type(); // updates the type

    String8 flags, type;
    const mat33& m(mMatrix);
    uint32_t orient = mType >> 8;

    if (orient&ROT_INVALID)
        flags.append("ROT_INVALID ");
    if (orient&ROT_90)
        flags.append("ROT_90 ");
    if (orient&FLIP_V)
        flags.append("FLIP_V ");
    if (orient&FLIP_H)
        flags.append("FLIP_H ");

    if (mType&SCALE)
        type.append("SCALE ");
    if (mType&ROTATE)
        type.append("ROTATE ");
    if (mType&TRANSLATE)
        type.append("TRANSLATE ");

    LOGD("%s (%s, %s)", name, flags.string(), type.string());
    LOGD("%.2f  %.2f  %.2f", m[0][0], m[1][0], m[2][0]);
    LOGD("%.2f  %.2f  %.2f", m[0][1], m[1][1], m[2][1]);
    LOGD("%.2f  %.2f  %.2f", m[0][2], m[1][2], m[2][2]);
}

// ---------------------------------------------------------------------------

}; // namespace android
