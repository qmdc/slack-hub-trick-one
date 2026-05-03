import request from '../request'
import type {ResponseData} from './types'

export function getPasswordList(category?: string): Promise<ResponseData<PasswordEntryDTO[]>> {
    return request.get('/password-entry/list', {params: {category}})
}

export function searchPasswords(keyword: string): Promise<ResponseData<PasswordEntryDTO[]>> {
    return request.get('/password-entry/search', {params: {keyword}})
}

export function getPasswordById(id: number): Promise<ResponseData<PasswordEntryDTO>> {
    return request.get(`/password-entry/${id}`)
}

export function createPassword(data: PasswordEntryCreateRequest): Promise<ResponseData<PasswordEntryDTO>> {
    return request.post('/password-entry', data)
}

export function updatePassword(id: number, data: PasswordEntryUpdateRequest): Promise<ResponseData<PasswordEntryDTO>> {
    return request.put(`/password-entry/${id}`, data)
}

export function deletePassword(id: number): Promise<ResponseData<void>> {
    return request.delete(`/password-entry/${id}`)
}

export function updateLastLoginTime(id: number): Promise<ResponseData<void>> {
    return request.post(`/password-entry/${id}/login`)
}

export function checkPasswordStrength(password: string): Promise<ResponseData<number>> {
    return request.post('/password-entry/check-strength', {password})
}

export function generatePassword(options: GeneratePasswordOptions): Promise<ResponseData<string>> {
    return request.post('/password-entry/generate', options)
}

export function getPasswordStatistics(): Promise<ResponseData<Record<string, number>>> {
    return request.get('/password-entry/statistics')
}

export function getCategoryList(): Promise<ResponseData<PasswordCategory[]>> {
    return request.get('/password-category/list')
}

export function createCategory(data: CategoryCreateRequest): Promise<ResponseData<PasswordCategory>> {
    return request.post('/password-category', data)
}

export function deleteCategory(id: number): Promise<ResponseData<void>> {
    return request.delete(`/password-category/${id}`)
}

// ============================================
// 类型定义
// ============================================

export interface PasswordEntryDTO {
    id: number
    website: string
    websiteName: string
    account: string
    password: string
    category: string
    categoryName: string
    passwordStrength: number
    passwordStrengthText: string
    lastLoginTime: number
    notes: string
    createTime: number
    updateTime: number
}

export interface PasswordEntryCreateRequest {
    websiteName: string
    website?: string
    account: string
    password: string
    category: string
    notes?: string
}

export interface PasswordEntryUpdateRequest {
    websiteName?: string
    website?: string
    account?: string
    password?: string
    category?: string
    notes?: string
}

export interface GeneratePasswordOptions {
    length?: number
    includeUppercase?: boolean
    includeLowercase?: boolean
    includeNumbers?: boolean
    includeSpecialChars?: boolean
}

export interface PasswordCategory {
    id: number
    userId: number
    name: string
    code: string
    color: string
    createTime: number
    updateTime: number
}

export interface CategoryCreateRequest {
    name: string
    code: string
    color?: string
}
